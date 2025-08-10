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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpigotAudienceProvider implements AudienceProvider, Listener {
    private final BukkitAudiences audiences;
    private final Plugin plugin;

    public SpigotAudienceProvider(Plugin plugin) {
        this.plugin = plugin;
        this.audiences = BukkitAudiences.create(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public @NotNull Audience console() {
        return audiences.console();
    }

    @Override
    public @NotNull Audience players() {
        return audiences.players();
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        return audiences.player(playerId);
    }

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return audiences.sender(sender);
    }

    @Override
    public Sound.@NotNull Emitter asEmitter(@NotNull Entity entity) {
        return BukkitAudiences.asEmitter(entity);
    }

    @Override
    public @NotNull Audience world(@NotNull Key world) {
        return audiences.world(world);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisable(PluginDisableEvent event) {
        if (event.getPlugin() == this.plugin) {
            audiences.close();
        }
    }
}
