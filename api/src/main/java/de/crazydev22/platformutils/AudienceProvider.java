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


import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Represents a provider for audiences, supporting multiple types of audiences such as the server's console,
 * online players, and players with specific permissions or in specific worlds.
 * <p>
 * The audiences retrieved by this provider are dynamically updated as the conditions for those audiences
 * (e.g., players joining or leaving, permission changes) change.
 */
public interface AudienceProvider {
    /**
     * Gets an audience for all online players, including the server's console.
     *
     * <p>The audience is dynamically updated as players join and leave.</p>
     *
     * @return the players' and console audience
     */
    default @NotNull Audience all() {
        return (ForwardingAudience) () -> List.of(console(), players());
    }

    /**
     * Gets an audience for the server's console.
     *
     * @return the console audience
     */
    @NotNull Audience console();

    /**
     * Gets an audience for all online players.
     *
     * <p>The audience is dynamically updated as players join and leave.</p>
     *
     * @return the players' audience
     */
    @NotNull Audience players();

    /**
     * Gets an audience for an individual player.
     *
     * <p>If the player is not online, messages are silently dropped.</p>
     *
     * @param playerId a player uuid
     * @return a player audience
     */
    @NotNull Audience player(final @NotNull UUID playerId);

    /**
     * Gets an audience for an individual player.
     *
     * <p>If the player is not online, messages are silently dropped.</p>
     *
     * @param player a player
     * @return a player audience
     */
    default @NotNull Audience player(final @NotNull Player player) {
        return this.player(player.getUniqueId());
    }

    /**
     * Gets an audience for the provided command sender.
     *
     * @param sender the command sender for which the audience is created
     * @return an audience representing the specified command sender
     */
    @NotNull Audience sender(final @NotNull CommandSender sender);

    /**
     * Converts an Entity instance into an Emitter instance, which can be used to represent the entity
     * as a sound emitter in the game's audio system.
     *
     * @param entity the entity to be converted to a sound emitter, must not be null
     * @return an Emitter instance representing the specified entity
     */
    Sound.@NotNull Emitter asEmitter(final @NotNull Entity entity);

    /**
     * Gets or creates an audience containing all viewers with the provided permission.
     *
     * <p>The audience is dynamically updated as permissions change.</p>
     *
     * @param permission the permission to filter sending to
     * @return a permissible audience
     */
    default @NotNull Audience permission(final @NotNull Key permission) {
        return this.permission(permission.namespace() + '.' + permission.value());
    }

    /**
     * Gets or creates an audience containing all viewers with the provided permission.
     *
     * <p>The audience is dynamically updated as permissions change.</p>
     *
     * @param permission the permission to filter sending to
     * @return a permissible audience
     */
    default @NotNull Audience permission(final @NotNull String permission) {
        return (ForwardingAudience.Single) () -> all().filterAudience(audience ->
                audience.get(PermissionChecker.POINTER)
                        .orElse(PermissionChecker.always(TriState.FALSE))
                        .test(permission));
    }

    /**
     * Gets an audience for online players in a world, including the server's console.
     *
     * <p>The audience is dynamically updated as players join and leave.</p>
     *
     * @param world identifier for a world
     * @return the world's audience
     */
    @NotNull Audience world(final @NotNull Key world);
}
