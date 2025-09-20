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
package de.crazydev22.platformutils.spigot;

import de.crazydev22.platformutils.AudienceProvider;
import de.crazydev22.platformutils.ItemEditor;
import de.crazydev22.platformutils.Platform;
import de.crazydev22.platformutils.Type;
import de.crazydev22.platformutils.scheduler.*;
import de.crazydev22.platformutils.spigot.scheduler.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.concurrent.CompletableFuture;

public class SpigotPlatform implements Platform {
    private final Server server;
    private final Plugin plugin;
    private final IAsyncScheduler async;
    private final IGlobalScheduler global;
    private final IRegionScheduler region;
    private final AudienceProvider provider;

    public SpigotPlatform(@NotNull Plugin plugin) {
        server = plugin.getServer();
        this.plugin = plugin;
        var scheduler = server.getScheduler();
        async = new SpigotAsyncScheduler(plugin, scheduler);
        global = new SpigotGlobalScheduler(plugin, scheduler);
        region = new SpigotRegionScheduler(global);
        provider = new SpigotAudienceProvider(plugin);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Type getType() {
        return Type.SPIGOT;
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Location location) {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Location location, int squareRadiusChunks) {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Block block) {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ) {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull World world, int chunkX, int chunkZ, int squareRadiusChunks) {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isOwnedByCurrentRegion(@NotNull Entity entity) {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isGlobalTickThread() {
        return server.isPrimaryThread();
    }

    @Override
    public boolean isTickThread() {
        return server.isPrimaryThread();
    }

    @Override
    public @NotNull IAsyncScheduler getAsyncScheduler() {
        return async;
    }

    @Override
    public @NotNull IEntityScheduler getEntityScheduler(@NotNull Entity entity) {
        return new SpigotEntityScheduler(global, entity);
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
        return getGlobalScheduler().<Boolean>run(task -> isValid(entity) && entity.teleport(location)).getResult().thenApply(b -> b != null ? b : false);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Chunk> getChunkAtAsync(@NotNull World world, int x, int z, boolean generate, boolean urgent) {
        if (isGlobalTickThread()) return CompletableFuture.completedFuture(world.getChunkAt(x, z));
        return global.run(() -> world.getChunkAt(x, z)).getResult();
    }

    @Override
    public boolean isChunkGenerated(@NotNull World world, int x, int z) {
        return world.isChunkGenerated(x, z);
    }

    @Override
    public @NotNull IRegionExecutor createRegionExecutor(@Range(from = 1, to = Integer.MAX_VALUE) int msPerTick) {
        return new SpigotRegionExecutor(this, msPerTick);
    }

    @Override
    public @NotNull ItemEditor editItem(@NotNull ItemStack item) {
        return new SpigotItemEditor(item);
    }

    @Override
    public @NotNull AudienceProvider getAudienceProvider() {
        return provider;
    }

    public static boolean isValid(Entity entity) {
        if (entity.isValid()) {
            return !(entity instanceof Player) || ((Player) entity).isOnline();
        }
        return entity instanceof Projectile && !entity.isDead();
    }
}
