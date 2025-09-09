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
package de.crazydev22.platformutils.spigot.scheduler;

import de.crazydev22.platformutils.scheduler.IRegionExecutor;
import de.crazydev22.platformutils.spigot.SpigotPlatform;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

@ApiStatus.Internal
public class SpigotRegionExecutor implements IRegionExecutor {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean closed = false;

    public SpigotRegionExecutor(SpigotPlatform platform, int msPerTick) {
        Bukkit.getScheduler().runTaskTimer(platform.getPlugin(), task -> {
            var time = System.currentTimeMillis() + msPerTick;
            while (time > System.currentTimeMillis()) {
                Runnable r = queue.poll();
                if (r == null) break;
                r.run();
            }

            if (closed && queue.isEmpty()) {
                task.cancel();
                latch.countDown();
            }
        }, 0, 0);
    }

    @Override
    public <T> CompletableFuture<T> queue(@NotNull World world, int chunkX, int chunkZ, @NotNull Callable<T> callable) {
        if (closed) throw new IllegalStateException("Executor is closed!");
        CompletableFuture<T> future = new CompletableFuture<>();
        queue.add(IRegionExecutor.complete(future, callable));
        return future;
    }

    @Override
    public void close() throws Exception {
        closed = true;
        latch.await();
    }
}
