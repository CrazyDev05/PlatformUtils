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
