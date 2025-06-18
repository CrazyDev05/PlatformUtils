package de.crazydev22.platformutils.paper.scheduler;

import de.crazydev22.platformutils.paper.PaperTask;
import de.crazydev22.platformutils.scheduler.IGlobalScheduler;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Ref;
import de.crazydev22.platformutils.scheduler.task.Task;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.Internal
public class PaperGlobalScheduler implements IGlobalScheduler {
    private final Plugin plugin;
    private final GlobalRegionScheduler scheduler;

    public PaperGlobalScheduler(Plugin plugin, GlobalRegionScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task,
                                                      @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        Ref<PaperTask.Completable<R>> ref = new Ref<>();
        return ref.value = new PaperTask.Completable<>(scheduler.runDelayed(plugin, t -> ref.value.complete(task), delayTicks), false);
    }

    @Override
    public @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task,
                                        @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                        @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        Ref<Task> ref = new Ref<>();
        return ref.value = new PaperTask(scheduler.runAtFixedRate(plugin, t -> task.accept(ref.value), initialDelayTicks, periodTicks), false);
    }
}
