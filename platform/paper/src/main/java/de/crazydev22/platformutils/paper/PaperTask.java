package de.crazydev22.platformutils.paper;

import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Task;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@ApiStatus.Internal
public class PaperTask implements Task {
    protected final ScheduledTask task;
    protected final boolean async;

    public PaperTask(@NotNull ScheduledTask task, boolean async) {
        this.task = task;
        this.async = async;
    }

    @Override
    public @NotNull Plugin getOwner() {
        return task.getOwningPlugin();
    }

    @Override
    public boolean isRepeating() {
        return task.isRepeatingTask();
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public @NotNull CancelledState cancel() {
        return wrapState(task.cancel());
    }

    @Override
    public @NotNull ExecutionState getExecutionState() {
        return wrapState(task.getExecutionState());
    }

    @ApiStatus.Internal
    public static class Completable<T> extends PaperTask implements CompletableTask<T> {
        private final CompletableFuture<T> result = new CompletableFuture<>();

        public Completable(@NotNull ScheduledTask task, boolean async) {
            super(task, async);
        }

        @Override
        public @NotNull CancelledState cancel() {
            ScheduledTask.CancelledState cancel = task.cancel();
            if (cancel == ScheduledTask.CancelledState.CANCELLED_BY_CALLER || cancel == ScheduledTask.CancelledState.CANCELLED_ALREADY) {
                result.cancel(false);
            }
            return wrapState(cancel);
        }

        @Override
        public void complete(@NotNull Function<CompletableTask<T>, T> function) {
            final CompletableFuture<T> result = getResult();
            if (result.isDone()) {
                return;
            }

            try {
                result.complete(function.apply(this));
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        }

        @Override
        public @NotNull CompletableFuture<T> getResult() {
            return result;
        }
    }

    private static CancelledState wrapState(ScheduledTask.CancelledState state) {
        return switch (state) {
            case CANCELLED_BY_CALLER -> CancelledState.CANCELLED_BY_CALLER;
            case CANCELLED_ALREADY -> CancelledState.CANCELLED_ALREADY;
            case RUNNING -> CancelledState.RUNNING;
            case ALREADY_EXECUTED -> CancelledState.ALREADY_EXECUTED;
            case NEXT_RUNS_CANCELLED -> CancelledState.NEXT_RUNS_CANCELLED;
            case NEXT_RUNS_CANCELLED_ALREADY -> CancelledState.NEXT_RUNS_CANCELLED_ALREADY;
        };
    }

    private static ExecutionState wrapState(ScheduledTask.ExecutionState state) {
        return switch (state) {
            case IDLE -> ExecutionState.IDLE;
            case RUNNING -> ExecutionState.RUNNING;
            case FINISHED -> ExecutionState.FINISHED;
            case CANCELLED -> ExecutionState.CANCELLED;
            case CANCELLED_RUNNING -> ExecutionState.CANCELLED_RUNNING;
        };
    }
}
