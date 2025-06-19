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
package de.crazydev22.platformutils.scheduler;

import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Task;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Scheduler that may be used to schedule tasks to execute asynchronously from the server tick process.
 */
@ApiStatus.NonExtendable
public interface IAsyncScheduler {
    /**
     * Schedules the specified task to be executed asynchronously immediately.
     *
     * @param task Specified task.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull Consumer<CompletableTask<Void>> task) {
        return run(t -> {
            task.accept(t);
            return null;
        });
    }

    /**
     * Schedules the specified task to be executed asynchronously immediately.
     *
     * @param task Specified task.
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull Runnable task) {
        return run(t -> {
            task.run();
            return null;
        });
    }

    /**
     * Schedules the specified task to be executed asynchronously immediately.
     *
     * @param <R>  Return type
     * @param task Specified task.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull Supplier<R> task) {
        return run(t -> {
            return task.get();
        });
    }

    /**
     * Schedules the specified task to be executed asynchronously immediately.
     *
     * @param <R>  Return type
     * @param task Specified task.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    @NotNull <R> CompletableTask<R> run(@NotNull Function<CompletableTask<R>, R> task);

    /**
     * Schedules the specified task to be executed asynchronously after the time delay has passed.
     *
     * @param task  Specified task.
     * @param delay The time delay to pass before the task should be executed.
     * @param unit  The time unit for the time delay.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull Consumer<CompletableTask<Void>> task,
                                                      @Range(from = 0, to = Long.MAX_VALUE) long delay,
                                                      @NotNull TimeUnit unit) {
        return runDelayed(t -> {
            task.accept(t);
            return null;
        }, delay, unit);
    }

    /**
     * Schedules the specified task to be executed asynchronously after the time delay has passed.
     *
     * @param task  Specified task.
     * @param delay The time delay to pass before the task should be executed.
     * @param unit  The time unit for the time delay.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull Runnable task,
                                                      @Range(from = 0, to = Long.MAX_VALUE) long delay,
                                                      @NotNull TimeUnit unit) {
        return runDelayed(t -> {
            task.run();
            return null;
        }, delay, unit);
    }

    /**
     * Schedules the specified task to be executed asynchronously after the time delay has passed.
     *
     * @param <R>   Return type
     * @param task  Specified task.
     * @param delay The time delay to pass before the task should be executed.
     * @param unit  The time unit for the time delay.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> runDelayed(@NotNull Supplier<R> task,
                                                       @Range(from = 0, to = Long.MAX_VALUE) long delay,
                                                       @NotNull TimeUnit unit) {
        return runDelayed(t -> {
            return task.get();
        }, delay, unit);
    }

    /**
     * Schedules the specified task to be executed asynchronously after the time delay has passed.
     *
     * @param <R>   Return type
     * @param task  Specified task.
     * @param delay The time delay to pass before the task should be executed.
     * @param unit  The time unit for the time delay.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task,
                                               @Range(from = 0, to = Long.MAX_VALUE) long delay,
                                               @NotNull TimeUnit unit);

    /**
     * Schedules the specified task to be executed asynchronously after the initial delay has passed,
     * and then periodically executed with the specified period.
     *
     * @param task         Specified task.
     * @param initialDelay The time delay to pass before the first execution of the task.
     * @param period       The time between task executions after the first execution of the task.
     * @param unit         The time unit for the initial delay and period.
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull Task runAtFixedRate(@NotNull Runnable task,
                                         @Range(from = 0, to = Long.MAX_VALUE) long initialDelay,
                                         @Range(from = 1, to = Long.MAX_VALUE) long period,
                                         @NotNull TimeUnit unit) {
        return runAtFixedRate(t -> task.run(), initialDelay, period, unit);
    }

    /**
     * Schedules the specified task to be executed asynchronously after the initial delay has passed,
     * and then periodically executed with the specified period.
     *
     * @param task         Specified task.
     * @param initialDelay The time delay to pass before the first execution of the task.
     * @param period       The time between task executions after the first execution of the task.
     * @param unit         The time unit for the initial delay and period.
     * @return The {@link Task} that represents the scheduled task.
     */
    @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task,
                                 @Range(from = 0, to = Long.MAX_VALUE) long initialDelay,
                                 @Range(from = 1, to = Long.MAX_VALUE) long period,
                                 @NotNull TimeUnit unit);
}
