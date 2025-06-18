package de.crazydev22.platformutils.spigot.scheduler;

import de.crazydev22.platformutils.scheduler.IGlobalScheduler;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Ref;
import de.crazydev22.platformutils.scheduler.task.Task;
import de.crazydev22.platformutils.spigot.SpigotTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotGlobalScheduler implements IGlobalScheduler {
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public SpigotGlobalScheduler(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task, @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        Ref<SpigotTask.Completable<R>> ref = new Ref<>();
        return ref.value = new SpigotTask.Completable<>(scheduler.runTaskLater(plugin, () -> ref.value.complete(task), delayTicks));
    }

    @Override
    public @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task, @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks, @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        Ref<SpigotTask> ref = new Ref<>();
        return ref.value = new SpigotTask(scheduler.runTaskTimer(plugin, () -> ref.value.run(task), initialDelayTicks, periodTicks), true);
    }
}
