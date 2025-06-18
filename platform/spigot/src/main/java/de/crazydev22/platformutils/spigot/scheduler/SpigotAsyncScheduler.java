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
        return ref.value = new SpigotTask.Completable<>(scheduler.runTaskAsynchronously(plugin, () -> ref.value.complete(task)));
    }

    @Override
    public @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task, @Range(from = 0, to = Long.MAX_VALUE) long delay, @NotNull TimeUnit unit) {
        Ref<SpigotTask.Completable<R>> ref = new Ref<>();
        return ref.value = new SpigotTask.Completable<>(scheduler.runTaskLaterAsynchronously(plugin, () -> ref.value.complete(task), unit.toMillis(delay) / 50));
    }

    @Override
    public @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task, @Range(from = 0, to = Long.MAX_VALUE) long initialDelay, @Range(from = 0, to = Long.MAX_VALUE) long period, @NotNull TimeUnit unit) {
        Ref<SpigotTask> ref = new Ref<>();
        return ref.value = new SpigotTask(scheduler.runTaskTimerAsynchronously(plugin, () -> ref.value.run(task), unit.toMillis(initialDelay) / 50, unit.toMillis(period) / 50), true);
    }
}
