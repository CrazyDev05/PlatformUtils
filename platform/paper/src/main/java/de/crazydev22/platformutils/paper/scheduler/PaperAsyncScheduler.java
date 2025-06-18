package de.crazydev22.platformutils.paper.scheduler;

import de.crazydev22.platformutils.paper.PaperTask;
import de.crazydev22.platformutils.scheduler.IAsyncScheduler;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Ref;
import de.crazydev22.platformutils.scheduler.task.Task;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.Internal
public class PaperAsyncScheduler implements IAsyncScheduler {
    private final Plugin plugin;
    private final AsyncScheduler scheduler;

    public PaperAsyncScheduler(Plugin plugin, AsyncScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public @NotNull <R> CompletableTask<R> run(@NotNull Function<CompletableTask<R>, R> task) {
        Ref<PaperTask.Completable<R>> ref = new Ref<>();
        return ref.value = new PaperTask.Completable<>(scheduler.runNow(plugin, t -> ref.value.complete(task)), true);
    }

    @Override
    public @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task,
                                                      @Range(from = 0, to = Long.MAX_VALUE) long delay,
                                                      @NotNull TimeUnit unit) {
        Ref<PaperTask.Completable<R>> ref = new Ref<>();
        return ref.value = new PaperTask.Completable<>(scheduler.runDelayed(plugin, t -> ref.value.complete(task), delay, unit), true);
    }

    @Override
    public @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task,
                                        @Range(from = 0, to = Long.MAX_VALUE) long initialDelay,
                                        @Range(from = 1, to = Long.MAX_VALUE) long period,
                                        @NotNull TimeUnit unit) {
        Ref<Task> ref = new Ref<>();
        return ref.value = new PaperTask(scheduler.runAtFixedRate(plugin, t -> task.accept(ref.value), initialDelay, period, unit), true);
    }
}
