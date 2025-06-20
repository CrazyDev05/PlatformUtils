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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An entity can move between worlds with an arbitrary tick delay, be temporarily removed
 * for players (i.e end credits), be partially removed from world state (i.e inactive but not removed),
 * teleport between ticking regions, teleport between worlds, and even be removed entirely from the server.
 * The uncertainty of an entity's state can make it difficult to schedule tasks without worrying about undefined
 * behaviors resulting from any of the states listed previously.
 *
 * <p>
 * This class is designed to eliminate those states by providing an interface to run tasks only when an entity
 * is contained in a world, on the owning thread for the region, and by providing the current Entity object.
 * The scheduler also allows a task to provide a callback, the "retired" callback, that will be invoked
 * if the entity is removed before a task that was scheduled could be executed. The scheduler is also
 * completely thread-safe, allowing tasks to be scheduled from any thread context. The scheduler also indicates
 * properly whether a task was scheduled successfully (i.e scheduler not retired), thus the code scheduling any task
 * knows whether the given callbacks will be invoked eventually or not - which may be critical for off-thread
 * contexts.
 * </p>
 */
@ApiStatus.NonExtendable
public interface IEntityScheduler {

    /**
     * Schedules a task to execute on the next tick. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param task    The task to execute
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable CompletableTask<Void> run(@NotNull Consumer<CompletableTask<Void>> task,
                                                @Nullable Runnable retired) {
        return run(t -> {
            task.accept(t);
            return null;
        }, retired);
    }

    /**
     * Schedules a task to execute on the next tick. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param task    The task to execute
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable CompletableTask<Void> run(@NotNull Runnable task,
                                                @Nullable Runnable retired) {
        return run(t -> {
            task.run();
            return null;
        }, retired);
    }

    /**
     * Schedules a task to execute on the next tick. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param <R>     Return type
     * @param task    The task to execute
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @return The {@link CompletableTask<R>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable <R> CompletableTask<R> run(@NotNull Supplier<R> task,
                                                 @Nullable Runnable retired) {
        return run(t -> {
            return task.get();
        }, retired);
    }

    /**
     * Schedules a task to execute on the next tick. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param <R>     Return type
     * @param task    The task to execute
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @return The {@link CompletableTask<R>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable <R> CompletableTask<R> run(@NotNull Function<CompletableTask<R>, R> task,
                                                 @Nullable Runnable retired) {
        return runDelayed(task, retired, 1);
    }

    /**
     * Schedules a task with the given delay. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param task       The task to execute
     * @param retired    Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable CompletableTask<Void> runDelayed(@NotNull Consumer<CompletableTask<Void>> task,
                                                       @Nullable Runnable retired,
                                                       @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(t -> {
            task.accept(t);
            return null;
        }, retired, delayTicks);
    }

    /**
     * Schedules a task with the given delay. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param task       The task to execute
     * @param retired    Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable CompletableTask<Void> runDelayed(@NotNull Runnable task,
                                                       @Nullable Runnable retired,
                                                       @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(t -> {
            task.run();
            return null;
        }, retired, delayTicks);
    }

    /**
     * Schedules a task with the given delay. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param <R>        Return type
     * @param task       The task to execute
     * @param retired    Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable <R> CompletableTask<R> runDelayed(@NotNull Supplier<R> task,
                                                        @Nullable Runnable retired,
                                                        @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(t -> {
            return task.get();
        }, retired, delayTicks);
    }

    /**
     * Schedules a task with the given delay. If the task failed to schedule because the scheduler is retired (entity
     * removed), then returns {@code null}. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param <R>        Return type
     * @param task       The task to execute
     * @param retired    Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    @Nullable <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task,
                                                @Nullable Runnable retired,
                                                @Range(from = 1, to = Long.MAX_VALUE) long delayTicks);

    /**
     * Schedules a repeating task with the given delay and period. If the task failed to schedule because the scheduler
     * is retired (entity removed), then returns {@code null}. Otherwise, either the task callback will be invoked after
     * the specified delay, or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param task              The task to execute
     * @param retired           Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    default @Nullable Task runAtFixedRate(@NotNull Runnable task,
                                          @Nullable Runnable retired,
                                          @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                          @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        return runAtFixedRate(t -> task.run(), retired, initialDelayTicks, periodTicks);
    }

    /**
     * Schedules a repeating task with the given delay and period. If the task failed to schedule because the scheduler
     * is retired (entity removed), then returns {@code null}. Otherwise, either the task callback will be invoked after
     * the specified delay, or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity, remove
     * other entities, load chunks, load worlds, modify ticket levels, etc.
     *
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     * </p>
     *
     * @param task              The task to execute
     * @param retired           Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task, or {@code null} if the entity has been removed.
     */
    @Nullable Task runAtFixedRate(@NotNull Consumer<Task> task,
                                  @Nullable Runnable retired,
                                  @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                  @Range(from = 1, to = Long.MAX_VALUE) long periodTicks);
}
