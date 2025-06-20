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
package de.crazydev22.platformutils.paper;

import de.crazydev22.platformutils.Platform;
import de.crazydev22.platformutils.paper.scheduler.PaperAsyncScheduler;
import de.crazydev22.platformutils.paper.scheduler.PaperEntityScheduler;
import de.crazydev22.platformutils.paper.scheduler.PaperGlobalScheduler;
import de.crazydev22.platformutils.paper.scheduler.PaperRegionScheduler;
import de.crazydev22.platformutils.scheduler.IAsyncScheduler;
import de.crazydev22.platformutils.scheduler.IEntityScheduler;
import de.crazydev22.platformutils.scheduler.IGlobalScheduler;
import de.crazydev22.platformutils.scheduler.IRegionScheduler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

@ApiStatus.Internal
public class PaperPlatform implements Platform {
    private final Plugin plugin;
    private final Server server;
    private final IAsyncScheduler async;
    private final IGlobalScheduler global;
    private final IRegionScheduler region;
    private final BooleanSupplier globalTickThread;

    public PaperPlatform(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        async = new PaperAsyncScheduler(plugin, server.getAsyncScheduler());
        global = new PaperGlobalScheduler(plugin, server.getGlobalRegionScheduler());
        region = new PaperRegionScheduler(plugin, server.getRegionScheduler());

        BooleanSupplier method;
        try {
            method = server::isGlobalTickThread;
        } catch (Throwable e) {
            method = server::isPrimaryThread;
        }
        globalTickThread = method;
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Location location) {
        return server.isOwnedByCurrentRegion(location);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Location location, int squareRadiusChunks) {
        return server.isOwnedByCurrentRegion(location, squareRadiusChunks);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Block block) {
        return server.isOwnedByCurrentRegion(block);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ) {
        return server.isOwnedByCurrentRegion(world, chunkX, chunkZ);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ, int squareRadiusChunks) {
        return server.isOwnedByCurrentRegion(world, chunkX, chunkZ, squareRadiusChunks);
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Entity entity) {
        return server.isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isGlobalTickThread() {
        return globalTickThread.getAsBoolean();
    }

    @Override
    public @NotNull IAsyncScheduler getAsyncScheduler() {
        return async;
    }

    @Override
    public @NotNull IEntityScheduler getEntityScheduler(@NotNull Entity entity) {
        return new PaperEntityScheduler(plugin, entity.getScheduler());
    }

    @Override
    public @NotNull IGlobalScheduler getGlobalScheduler() {
        return global;
    }

    @Override
    public @NotNull IRegionScheduler getRegionScheduler() {
        return region;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> teleportAsync(@NotNull Entity entity, @NotNull Location location, PlayerTeleportEvent.@NotNull TeleportCause cause) {
        return entity.teleportAsync(location, cause);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean generate, boolean urgent) {
        return world.getChunkAtAsync(x, z, generate, urgent);
    }

    @Override
    public boolean isChunkGenerated(@NotNull World world, int x, int z) {
        return world.isChunkGenerated(x, z) ;
    }
}
