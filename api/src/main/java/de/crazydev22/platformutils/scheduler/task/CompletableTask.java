package de.crazydev22.platformutils.scheduler.task;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents a task that can be explicitly completed with a result. This interface extends
 * {@link Task} and provides additional functionality for managing tasks whose completion can
 * be triggered programmatically.
 *
 * @param <T> the type of the result produced upon completing the task.
 */
@ApiStatus.NonExtendable
public interface CompletableTask<T> extends Task {

    /**
     * Retrieves the {@link CompletableFuture} associated with the task, representing
     * the eventual completion result of the task. The returned future does not complete
     * until the task is either completed successfully or exceptionally.
     *
     * @return a {@link CompletableFuture} representing the result of the task upon completion.
     */
    @NotNull CompletableFuture<T> getResult();

    /**
     * Completes the task by supplying a result computed through the provided function. The function receives
     * the current {@code CompletableTask} instance as its input and is expected to produce the task's result.
     * If the task has already been completed, this method does nothing.
     * If the function throws an exception, the task's completion will reflect the exception.
     *
     * @param function the function that computes the result of the task. The function accepts the current
     *                 {@code CompletableTask} as its input and returns the result to be used for completing
     *                 the task. Must not be {@code null}.
     */
    @ApiStatus.Internal
    void complete(@NotNull Function<CompletableTask<T>, T> function);
}
