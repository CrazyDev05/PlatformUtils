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

import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Represents an interface for executing tasks associated with specific regions in a world,
 * categorized by chunk coordinates. Tasks queued through this executor are synchronized
 * with the respective chunk context, ensuring controlled and thread-safe execution.
 * This interface primarily supports asynchronous task scheduling and execution.
 */
public interface IRegionExecutor extends AutoCloseable {

    /**
     * Queues a runnable task for execution in a specific world at the specified chunk coordinates.
     * This method allows asynchronous tasks to be executed in a controlled manner, ensuring
     * proper synchronization with the specified chunk context.
     *
     * @param world   The world in which the task should be executed. Must not be null.
     * @param chunkX  The X coordinate of the chunk associated with the task.
     * @param chunkZ  The Z coordinate of the chunk associated with the task.
     * @param runnable The runnable task to be executed. Must not be null.
     * @return A {@link CompletableFuture} that represents the result of the task execution.
     */
    default CompletableFuture<?> queue(@NotNull World world, int chunkX, int chunkZ, @NotNull Runnable runnable) {
        return queue(world, chunkX, chunkZ, Executors.callable(runnable));
    }

    /**
     * Queues a runnable task for execution in a specific world at the specified chunk coordinates.
     * This method allows asynchronous tasks to be executed in a controlled manner, ensuring
     * proper synchronization with the specified chunk context. The provided result object will
     * be used as the result of the task execution.
     *
     * @param <T>     The return type associated with the task execution.
     * @param world   The world in which the task should be executed. Must not be null.
     * @param chunkX  The X coordinate of the chunk associated with the task.
     * @param chunkZ  The Z coordinate of the chunk associated with the task.
     * @param runnable The runnable task to be executed. Must not be null.
     * @param result  The result object to be used for the task execution. May be null.
     * @return A {@link CompletableFuture} that represents the result of the task execution.
     */
    default <T> CompletableFuture<T> queue(@NotNull World world, int chunkX, int chunkZ, @NotNull Runnable runnable, @Nullable T result) {
        return queue(world, chunkX, chunkZ, Executors.callable(runnable, result));
    }

    /**
     * Queues a callable task for execution in a specific world at the specified chunk coordinates.
     * This method allows asynchronous tasks to be executed in a controlled manner, ensuring
     * proper synchronization with the specified chunk context.
     *
     * @param <T>      The return type of the callable task.
     * @param world    The world in which the task should be executed. Must not be null.
     * @param chunkX   The X coordinate of the chunk associated with the task.
     * @param chunkZ   The Z coordinate of the chunk associated with the task.
     * @param callable The callable task to be executed. Must not be null.
     * @return A {@link CompletableFuture} that represents the result of the callable task execution.
     */
    <T> CompletableFuture<T> queue(@NotNull World world, int chunkX, int chunkZ, @NotNull Callable<T> callable);

    /**
     * Creates a {@link Runnable} that completes the provided {@link CompletableFuture} with
     * the result of the given {@link Callable}. If the {@link Callable} throws an exception,
     * the {@link CompletableFuture} is completed exceptionally with the thrown exception.
     *
     * @param <T>      The type of result produced by the {@link Callable} and completed in the {@link CompletableFuture}.
     * @param future   The {@link CompletableFuture} to be completed with the result or exception of the {@link Callable}. Must not be null.
     * @param callable The {@link Callable} whose result or exception will be used to complete the {@link CompletableFuture}. Must not be null.
     * @return A {@link Runnable} that, when executed, completes the {@link CompletableFuture} with the result or exception of the {@link Callable}.
     */
    @ApiStatus.Internal
    static <T> Runnable complete(CompletableFuture<T> future, Callable<T> callable) {
        return () -> {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        };
    }
}
