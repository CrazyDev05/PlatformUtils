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
package de.crazydev22.platformutils;

import de.crazydev22.platformutils.scheduler.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a platform interface that provides various utility methods
 * for interacting with server regions, scheduling tasks, and other
 * server-related functionalities.
 */
@ApiStatus.NonExtendable
public interface Platform {
    /**
     * Retrieves the current plugin instance.
     *
     * @return the Plugin instance associated with this context.
     */
    Plugin getPlugin();

    /**
     * Folia: Returns whether the current thread is ticking a region and that
     * the region being ticked owns the chunk at the specified world and block
     * position as included in the specified location.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @param location Specified location, must have a non-null world
     * @return true if the current thread is ticking the region that owns the chunk at the specified location
     */
    boolean isOwnedByCurrentRegion(@NotNull Location location);

    /**
     * Folia: Returns whether the current thread is ticking a region and that
     * the region being ticked owns the chunks centered at the specified world
     * and block position as included in the specified location within the
     * specified square radius. Specifically, this function checks that every
     * chunk with position x in [centerX - radius, centerX + radius] and
     * position z in [centerZ - radius, centerZ + radius] is owned by the
     * current ticking region.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @param location           Specified location, must have a non-null world
     * @param squareRadiusChunks Specified square radius. Must be >= 0. Note that this parameter is not a squared radius, but rather a Chebyshev Distance
     * @return true if the current thread is ticking the region that owns the chunks centered at the specified location within the specified square radius
     */
    boolean isOwnedByCurrentRegion(@NotNull Location location, int squareRadiusChunks);

    /**
     * Folia: Returns whether the current thread is ticking a region and that
     * the region being ticked owns the chunk at the specified block position.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @param block Specified block position
     * @return true if the current thread is ticking the region that owns the chunk at the specified block position
     */
    boolean isOwnedByCurrentRegion(@NotNull Block block);

    /**
     * Folia: Returns whether the current thread is ticking a region and that
     * the region being ticked owns the chunk at the specified world and chunk
     * position.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @param world  Specified world
     * @param chunkX Specified x-coordinate of the chunk position
     * @param chunkZ Specified z-coordinate of the chunk position
     * @return true if the current thread is ticking the region that owns the chunk at the specified world and chunk position
     */
    boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ);

    /**
     * Folia: Returns whether the current thread is ticking a region and that
     * the region being ticked owns the chunks centered at the specified world
     * and chunk position within the specified square radius. Specifically,
     * this function checks that every chunk with position x in [centerX -
     * radius, centerX + radius] and position z in [centerZ - radius, centerZ +
     * radius] is owned by the current ticking region.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @param world              Specified world
     * @param chunkX             Specified x-coordinate of the chunk position
     * @param chunkZ             Specified z-coordinate of the chunk position
     * @param squareRadiusChunks Specified square radius. Must be >= 0. Note that this parameter is not a squared radius, but rather a Chebyshev Distance.
     * @return true if the current thread is ticking the region that owns the chunks centered at the specified world and chunk position within the specified square radius
     */
    boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ, int squareRadiusChunks);

    /**
     * Folia: Returns whether the current thread is ticking a region and that
     * the region being ticked owns the specified entity. Note that this
     * function is the only appropriate method of checking for ownership of an
     * entity, as retrieving the entity's location is undefined unless the
     * entity is owned by the current region.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @param entity Specified entity
     * @return true if the current thread is ticking the region that owns the specified entity
     */
    boolean isOwnedByCurrentRegion(@NotNull Entity entity);

    /**
     * Folia: Returns whether the current thread is ticking the global region.
     * Paper/Spigot: Returns {@link Server#isPrimaryThread()}
     *
     * @return true if the current thread is ticking the global region
     */
    boolean isGlobalTickThread();

    /**
     * Scheduler that may be used to schedule tasks to execute asynchronously from the server tick process.
     *
     * @return the async scheduler
     */
    @NotNull IAsyncScheduler getAsyncScheduler();

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
     *
     * @param entity entity to get the scheduler for
     * @return the entity's scheduler
     */
    @NotNull IEntityScheduler getEntityScheduler(@NotNull Entity entity);

    /**
     * The global region task scheduler may be used to schedule tasks that will execute on the global region.
     * <p>
     * The global region is responsible for maintaining world day time, world game time, weather cycle,
     * sleep night skipping, executing commands for console, and other misc. tasks that do not belong to any specific region.
     * </p>
     *
     * @return the global region scheduler
     */
    @NotNull IGlobalScheduler getGlobalScheduler();

    /**
     * The region task scheduler can be used to schedule tasks by location to be executed on the region which owns the location.
     * <p>
     * <b>Note</b>: It is entirely inappropriate to use the region scheduler to schedule tasks for entities.
     * If you wish to schedule tasks to perform actions on entities, you should be using {@link Platform#getEntityScheduler(Entity)}
     * as the entity scheduler will "follow" an entity if it is teleported, whereas the region task scheduler
     * will not.
     * </p>
     *
     * @return the region scheduler
     */
    @NotNull IRegionScheduler getRegionScheduler();

    /**
     * Teleport an entity to a location async
     *
     * @param entity   Entity to teleport
     * @param location Location to teleport to
     * @return Future when the teleport is completed or failed
     */
    default @NotNull CompletableFuture<@NotNull Boolean> teleportAsync(@NotNull Entity entity, @NotNull Location location) {
        return teleportAsync(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleport an entity to a location async with a cause
     *
     * @param entity   Entity to teleport
     * @param location Location to teleport to
     * @param cause    Cause of the teleport
     * @return Future when the teleport is completed or failed
     */
    @NotNull CompletableFuture<@NotNull Boolean> teleportAsync(@NotNull Entity entity, @NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause);


    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param location Location to get chunk for
     * @return {@link CompletableFuture<Chunk>} that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    default @NotNull CompletableFuture<@Nullable Chunk> getChunkAtAsync(@NotNull Location location) {
        return getChunkAtAsync(location, true, false);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param location Location to get chunk for
     * @param generate Should the chunk generate
     * @return {@link CompletableFuture<Chunk>} that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    default @NotNull CompletableFuture<@Nullable Chunk> getChunkAtAsync(@NotNull Location location, boolean generate) {
        return getChunkAtAsync(location, generate, false);
    }


    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param location Location to get chunk for
     * @param generate Should the chunk generate
     * @param urgent   use high priority for chunk load ticket
     * @return {@link CompletableFuture<Chunk>} that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    default @NotNull CompletableFuture<@Nullable Chunk> getChunkAtAsync(@NotNull Location location, boolean generate, boolean urgent) {
        return getChunkAtAsync(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, generate, urgent);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param world    World to load chunk for
     * @param x        X coordinate of the chunk to load
     * @param z        Z coordinate of the chunk to load
     * @return {@link CompletableFuture<Chunk>} that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    default @NotNull CompletableFuture<@Nullable Chunk> getChunkAtAsync(@NotNull World world, int x, int z) {
        return getChunkAtAsync(world, x, z, true);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param world    World to load chunk for
     * @param x        X coordinate of the chunk to load
     * @param z        Z coordinate of the chunk to load
     * @param generate Should the chunk generate
     * @return {@link CompletableFuture<Chunk>} that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    default @NotNull CompletableFuture<@Nullable Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean generate) {
        return getChunkAtAsync(world, x, z, generate, false);
    }

    /**
     * Gets the chunk at the target location, loading it asynchronously if needed.
     *
     * @param world    World to load chunk for
     * @param x        X coordinate of the chunk to load
     * @param z        Z coordinate of the chunk to load
     * @param generate Should the chunk generate
     * @param urgent   use high priority for chunk load ticket
     * @return {@link CompletableFuture<Chunk>} that completes with the chunk, or null if the chunk did not exists and generation was not requested.
     */
    @NotNull CompletableFuture<@Nullable Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean generate, boolean urgent);

    /**
     * Checks if the chunk containing the specified location has been generated.
     *
     * @param location the location to check
     * @return true if the chunk at the specified location has been generated, false otherwise
     */
    default boolean isChunkGenerated(@NotNull Location location) {
        return isChunkGenerated(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ());
    }

    /**
     * Checks whether the chunk at the specified coordinates within the given world has been generated.
     *
     * @param world the world in which the chunk is located
     * @param x     the x-coordinate of the chunk
     * @param z     the z-coordinate of the chunk
     * @return true if the chunk at the specified location has been generated, false otherwise
     */
    boolean isChunkGenerated(@NotNull World world, int x, int z);

    /**
     * Creates and returns an instance of {@link IRegionExecutor} which is responsible for
     * managing and executing tasks in a region over a specified time period.
     *
     * @param msPerTick the maximum number of milliseconds that the executor
     *                  is allowed to spend per tick. Must be a positive integer.
     * @return an instance of IRegionExecutor configured with the specified
     *         maximum time per tick.
     */
    @NotNull IRegionExecutor createRegionExecutor(@Range(from = 1, to = Integer.MAX_VALUE) int msPerTick);

    /**
     * Edits the provided item and returns an editor instance for further modifications.
     *
     * @param item the item to edit, must not be null
     * @return an instance of ItemEditor for modifying the specified item
     */
    @NotNull ItemEditor editItem(@NotNull ItemStack item);

    /**
     * Retrieves the audience provider, which is responsible for managing and delivering
     * communication to the intended audience.
     *
     * @return the {@link AudienceProvider} of this platform instance.
     */
    @NotNull AudienceProvider getAudienceProvider();
}
