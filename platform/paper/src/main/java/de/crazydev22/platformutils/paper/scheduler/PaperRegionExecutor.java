/*
 * MIT License
 *
 * Copyright (c) 2025 Julian Krings
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.crazydev22.platformutils.paper.scheduler;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import de.crazydev22.platformutils.paper.PaperPlatform;
import de.crazydev22.platformutils.scheduler.IRegionExecutor;
import de.crazydev22.platformutils.scheduler.IRegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@ApiStatus.Internal
public class PaperRegionExecutor implements IRegionExecutor, Listener {
    private final Semaphore latch = new Semaphore(Integer.MAX_VALUE, true);
    private final ThreadLocal<Long> limit;

    private final IRegionScheduler scheduler;
    private final Map<Key, Queue<Runnable>> queues;
    private volatile boolean closed = false;

    public PaperRegionExecutor(PaperPlatform platform, int msPerTick) {
        this.scheduler = platform.getRegionScheduler();
        this.limit = ThreadLocal.withInitial(() -> System.currentTimeMillis() + msPerTick);
        this.queues = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, platform.getPlugin());
    }

    @Override
    public <T> CompletableFuture<T> queue(@NotNull World world, int chunkX, int chunkZ, @NotNull Callable<T> callable) {
        if (closed) throw new IllegalStateException("Executor is closed!");
        CompletableFuture<T> future = new CompletableFuture<>();
        queues.computeIfAbsent(new Key(world, chunkX, chunkZ), this::createExecutor)
                .add(IRegionExecutor.complete(future, callable));
        return future;
    }

    private Queue<Runnable> createExecutor(Key key) {
        latch.acquireUninterruptibly();
        var queue = new ConcurrentLinkedQueue<Runnable>();
        scheduler.runAtFixedRate(key.world, key.x, key.z, t -> {
            var time = limit.get();
            while (time > System.currentTimeMillis()) {
                Runnable r = queue.poll();
                if (r == null) break;
                r.run();
            }

            if (closed && queue.isEmpty()) {
                t.cancel();
                latch.release();
                queues.remove(key);
            }
        }, 1, 1);
        return queue;
    }

    @Override
    public void close() {
        closed = true;
        latch.acquireUninterruptibly(Integer.MAX_VALUE);
        latch.release(Integer.MAX_VALUE);
    }

    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        limit.remove();
    }

    private record Key(World world, int x, int z) {}
}
