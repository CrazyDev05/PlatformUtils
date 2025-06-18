package de.crazydev22.platformutils.spigot;

import de.crazydev22.platformutils.Platform;
import de.crazydev22.platformutils.scheduler.IAsyncScheduler;
import de.crazydev22.platformutils.scheduler.IEntityScheduler;
import de.crazydev22.platformutils.scheduler.IGlobalScheduler;
import de.crazydev22.platformutils.scheduler.IRegionScheduler;
import de.crazydev22.platformutils.spigot.scheduler.SpigotAsyncScheduler;
import de.crazydev22.platformutils.spigot.scheduler.SpigotEntityScheduler;
import de.crazydev22.platformutils.spigot.scheduler.SpigotGlobalScheduler;
import de.crazydev22.platformutils.spigot.scheduler.SpigotRegionScheduler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SpigotPlatform implements Platform {
    private final Server server;
    private final IAsyncScheduler async;
    private final IGlobalScheduler global;
    private final IRegionScheduler region;

    public SpigotPlatform(@NotNull Plugin plugin) {
        server = plugin.getServer();
        var scheduler = server.getScheduler();
        async = new SpigotAsyncScheduler(plugin, scheduler);
        global = new SpigotGlobalScheduler(plugin, scheduler);
        region = new SpigotRegionScheduler(global);
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

    public static boolean isValid(Entity entity) {
        if (entity.isValid()) {
            return !(entity instanceof Player) || ((Player) entity).isOnline();
        }
        return entity instanceof Projectile && !entity.isDead();
    }
}
