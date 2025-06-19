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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The global region task scheduler may be used to schedule tasks that will execute on the global region.
 * <p>
 * The global region is responsible for maintaining world day time, world game time, weather cycle,
 * sleep night skipping, executing commands for console, and other misc. tasks that do not belong to any specific region.
 * </p>
 */
@ApiStatus.NonExtendable
public interface IGlobalScheduler {
    /**
     * Schedules a task to be executed on the global region on the next tick.
     *
     * @param task The task to execute
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull Consumer<CompletableTask<Void>> task) {
        return run(t -> {
            task.accept(t);
            return null;
        });
    }

    /**
     * Schedules a task to be executed on the global region on the next tick.
     *
     * @param task The task to execute
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull Task run(@NotNull Runnable task) {
        return run(t -> {
            task.run();
            return null;
        });
    }

    /**
     * Schedules a task to be executed on the global region on the next tick.
     *
     * @param <R>  Return type
     * @param task The task to execute
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull Supplier<R> task) {
        return run(t -> {
            return task.get();
        });
    }

    /**
     * Schedules a task to be executed on the global region on the next tick.
     *
     * @param <R>  Return type
     * @param task The task to execute
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull Function<CompletableTask<R>, R> task) {
        return runDelayed(task, 1);
    }

    /**
     * Schedules a task to be executed on the global region after the specified delay in ticks.
     *
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull Consumer<CompletableTask<Void>> task,
                                                      @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(t -> {
            task.accept(t);
            return null;
        }, delayTicks);
    }

    /**
     * Schedules a task to be executed on the global region after the specified delay in ticks.
     *
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull Runnable task,
                                                      @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(t -> {
            task.run();
            return null;
        }, delayTicks);
    }

    /**
     * Schedules a task to be executed on the global region after the specified delay in ticks.
     *
     * @param <R>        Return type
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> runDelayed(@NotNull Supplier<R> task,
                                                       @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(t -> {
            return task.get();
        }, delayTicks);
    }

    /**
     * Schedules a task to be executed on the global region after the specified delay in ticks.
     *
     * @param <R>        Return type
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    @NotNull <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task,
                                               @Range(from = 1, to = Long.MAX_VALUE) long delayTicks);

    /**
     * Schedules a repeating task to be executed on the global region after the initial delay with the
     * specified period.
     *
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull Task runAtFixedRate(@NotNull Runnable task,
                                         @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                         @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        return runAtFixedRate(t -> task.run(), initialDelayTicks, periodTicks);
    }

    /**
     * Schedules a repeating task to be executed on the global region after the initial delay with the
     * specified period.
     *
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task.
     */
    @NotNull Task runAtFixedRate(@NotNull Consumer<Task> task,
                                 @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                 @Range(from = 1, to = Long.MAX_VALUE) long periodTicks);
}
