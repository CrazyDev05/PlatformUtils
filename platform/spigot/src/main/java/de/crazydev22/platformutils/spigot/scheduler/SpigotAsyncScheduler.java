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

import de.crazydev22.platformutils.scheduler.IAsyncScheduler;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Ref;
import de.crazydev22.platformutils.scheduler.task.Task;
import de.crazydev22.platformutils.spigot.SpigotTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotAsyncScheduler implements IAsyncScheduler {
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public SpigotAsyncScheduler(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public @NotNull <R> CompletableTask<R> run(@NotNull Function<CompletableTask<R>, R> task) {
        Ref<SpigotTask.Completable<R>> ref = new Ref<>();
        return ref.set(new SpigotTask.Completable<>(scheduler.runTaskAsynchronously(plugin, ref.run(task, SpigotTask.Completable::complete))));
    }

    @Override
    public @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task, @Range(from = 0, to = Long.MAX_VALUE) long delay, @NotNull TimeUnit unit) {
        Ref<SpigotTask.Completable<R>> ref = new Ref<>();
        return ref.set(new SpigotTask.Completable<>(scheduler.runTaskLaterAsynchronously(plugin, ref.run(task, SpigotTask.Completable::complete), unit.toMillis(delay) / 50)));
    }

    @Override
    public @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task, @Range(from = 0, to = Long.MAX_VALUE) long initialDelay, @Range(from = 0, to = Long.MAX_VALUE) long period, @NotNull TimeUnit unit) {
        Ref<Task> ref = new Ref<>();
        return ref.set(new SpigotTask(scheduler.runTaskTimerAsynchronously(plugin, ref.run(task), unit.toMillis(initialDelay) / 50, unit.toMillis(period) / 50), true));
    }
}
