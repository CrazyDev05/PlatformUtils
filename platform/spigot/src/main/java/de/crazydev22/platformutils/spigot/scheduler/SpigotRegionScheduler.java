package de.crazydev22.platformutils.spigot.scheduler;

import de.crazydev22.platformutils.scheduler.IGlobalScheduler;
import de.crazydev22.platformutils.scheduler.IRegionScheduler;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Task;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotRegionScheduler implements IRegionScheduler {
    private final IGlobalScheduler scheduler;

    public SpigotRegionScheduler(IGlobalScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public @NotNull <R> CompletableTask<R> runDelayed(@NotNull World world, int chunkX, int chunkZ, @NotNull Function<CompletableTask<R>, R> task, @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return scheduler.runDelayed(task, delayTicks);
    }

    @Override
    public @NotNull Task runAtFixedRate(@NotNull World world, int chunkX, int chunkZ, @NotNull Consumer<Task> task, @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks, @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        return scheduler.runAtFixedRate(task, initialDelayTicks, periodTicks);
    }
}
