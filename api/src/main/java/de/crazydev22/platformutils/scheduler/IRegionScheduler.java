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

import de.crazydev22.platformutils.Platform;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Task;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The region task scheduler can be used to schedule tasks by location to be executed on the region which owns the location.
 * <p>
 * <b>Note</b>: It is entirely inappropriate to use the region scheduler to schedule tasks for entities.
 * If you wish to schedule tasks to perform actions on entities, you should be using {@link Platform#getEntityScheduler(Entity)}
 * as the entity scheduler will "follow" an entity if it is teleported, whereas the region task scheduler
 * will not.
 * </p>
 */
@ApiStatus.NonExtendable
public interface IRegionScheduler {
    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param world  The world of the region that owns the task
     * @param chunkX The chunk X coordinate of the region that owns the task
     * @param chunkZ The chunk Z coordinate of the region that owns the task
     * @param task   The task to execute
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull World world,
                                               int chunkX,
                                               int chunkZ,
                                               @NotNull Consumer<CompletableTask<Void>> task) {
        return run(world, chunkX, chunkZ, t -> {
            task.accept(t);
            return null;
        });
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param world  The world of the region that owns the task
     * @param chunkX The chunk X coordinate of the region that owns the task
     * @param chunkZ The chunk Z coordinate of the region that owns the task
     * @param task   The task to execute
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull World world,
                                               int chunkX,
                                               int chunkZ,
                                               @NotNull Runnable task) {
        return run(world, chunkX, chunkZ, t -> {
            task.run();
            return null;
        });
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param <R>    Return type
     * @param world  The world of the region that owns the task
     * @param chunkX The chunk X coordinate of the region that owns the task
     * @param chunkZ The chunk Z coordinate of the region that owns the task
     * @param task   The task to execute
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull World world,
                                                int chunkX,
                                                int chunkZ,
                                                @NotNull Supplier<R> task) {
        return run(world, chunkX, chunkZ, t -> {
            return task.get();
        });
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param <R>    Return type
     * @param world  The world of the region that owns the task
     * @param chunkX The chunk X coordinate of the region that owns the task
     * @param chunkZ The chunk Z coordinate of the region that owns the task
     * @param task   The task to execute
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull World world,
                                                int chunkX,
                                                int chunkZ,
                                                @NotNull Function<CompletableTask<R>, R> task) {
        return runDelayed(world, chunkX, chunkZ, task, 1);
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param location The location at which the region executing should own
     * @param task     The task to execute
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull Location location,
                                               @NotNull Consumer<CompletableTask<Void>> task) {
        return run(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param location The location at which the region executing should own
     * @param task     The task to execute
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> run(@NotNull Location location,
                                               @NotNull Runnable task) {
        return run(location, t -> {
            task.run();
        });
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param <R>      Return type
     * @param location The location at which the region executing should own
     * @param task     The task to execute
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull Location location,
                                                @NotNull Supplier<R> task) {
        return run(location, t -> {
            return task.get();
        });
    }

    /**
     * Schedules a task to be executed on the region which owns the location on the next tick.
     *
     * @param <R>      Return type
     * @param location The location at which the region executing should own
     * @param task     The task to execute
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> run(@NotNull Location location,
                                                @NotNull Function<CompletableTask<R>, R> task) {
        return run(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param world      The world of the region that owns the task
     * @param chunkX     The chunk X coordinate of the region that owns the task
     * @param chunkZ     The chunk Z coordinate of the region that owns the task
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull World world,
                                                      int chunkX,
                                                      int chunkZ,
                                                      @NotNull Consumer<CompletableTask<Void>> task,
                                                      @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(world, chunkX, chunkZ, t -> {
            task.accept(t);
            return null;
        }, delayTicks);
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param world      The world of the region that owns the task
     * @param chunkX     The chunk X coordinate of the region that owns the task
     * @param chunkZ     The chunk Z coordinate of the region that owns the task
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull World world,
                                                      int chunkX,
                                                      int chunkZ,
                                                      @NotNull Runnable task,
                                                      @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(world, chunkX, chunkZ, t -> {
            task.run();
            return null;
        }, delayTicks);
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param <R>        Return type
     * @param world      The world of the region that owns the task
     * @param chunkX     The chunk X coordinate of the region that owns the task
     * @param chunkZ     The chunk Z coordinate of the region that owns the task
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> runDelayed(@NotNull World world,
                                                       int chunkX,
                                                       int chunkZ,
                                                       @NotNull Supplier<R> task,
                                                       @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(world, chunkX, chunkZ, t -> {
            return task.get();
        }, delayTicks);
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param <R>        Return type
     * @param world      The world of the region that owns the task
     * @param chunkX     The chunk X coordinate of the region that owns the task
     * @param chunkZ     The chunk Z coordinate of the region that owns the task
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    @NotNull <R> CompletableTask<R> runDelayed(@NotNull World world,
                                               int chunkX,
                                               int chunkZ,
                                               @NotNull Function<CompletableTask<R>, R> task,
                                               @Range(from = 1, to = Long.MAX_VALUE) long delayTicks);

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param location   The location at which the region executing should own
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<Void>} that represents the scheduled task.
     */
    default @NotNull CompletableTask<Void> runDelayed(@NotNull Location location,
                                                      @NotNull Consumer<CompletableTask<Void>> task,
                                                      @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delayTicks);
    }

    /**
     * Schedules a task to be executed on the region which owns the location after the specified delay in ticks.
     *
     * @param <R>        Return type
     * @param location   The location at which the region executing should own
     * @param task       The task to execute
     * @param delayTicks The delay, in ticks.
     * @return The {@link CompletableTask<R>} that represents the scheduled task.
     */
    default @NotNull <R> CompletableTask<R> runDelayed(@NotNull Location location,
                                                       @NotNull Function<CompletableTask<R>, R> task,
                                                       @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        return runDelayed(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delayTicks);
    }

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the
     * specified period.
     *
     * @param world             The world of the region that owns the task
     * @param chunkX            The chunk X coordinate of the region that owns the task
     * @param chunkZ            The chunk Z coordinate of the region that owns the task
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull Task runAtFixedRate(@NotNull World world,
                                         int chunkX,
                                         int chunkZ,
                                         @NotNull Runnable task,
                                         @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                         @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        return runAtFixedRate(world, chunkX, chunkZ, t -> task.run(), initialDelayTicks, periodTicks);
    }

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the
     * specified period.
     *
     * @param world             The world of the region that owns the task
     * @param chunkX            The chunk X coordinate of the region that owns the task
     * @param chunkZ            The chunk Z coordinate of the region that owns the task
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task.
     */
    @NotNull Task runAtFixedRate(@NotNull World world,
                                 int chunkX,
                                 int chunkZ,
                                 @NotNull Consumer<Task> task,
                                 @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                 @Range(from = 1, to = Long.MAX_VALUE) long periodTicks);

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the
     * specified period.
     *
     * @param location          The location at which the region executing should own
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull Task runAtFixedRate(@NotNull Location location,
                                         @NotNull Runnable task,
                                         @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                         @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        return runAtFixedRate(location, t -> task.run(), initialDelayTicks, periodTicks);
    }

    /**
     * Schedules a repeating task to be executed on the region which owns the location after the initial delay with the
     * specified period.
     *
     * @param location          The location at which the region executing should own
     * @param task              The task to execute
     * @param initialDelayTicks The initial delay, in ticks.
     * @param periodTicks       The period, in ticks.
     * @return The {@link Task} that represents the scheduled task.
     */
    default @NotNull Task runAtFixedRate(@NotNull Location location,
                                         @NotNull Consumer<Task> task,
                                         @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks,
                                         @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        return runAtFixedRate(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, initialDelayTicks, periodTicks);
    }
}
