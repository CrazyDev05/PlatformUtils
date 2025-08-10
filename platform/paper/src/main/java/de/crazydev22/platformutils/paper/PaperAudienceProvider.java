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

import de.crazydev22.platformutils.AudienceProvider;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class PaperAudienceProvider implements AudienceProvider {

    @Override
    public @NotNull Audience console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public @NotNull Audience players() {
        return (ForwardingAudience) () -> (Iterable<? extends Audience>) Bukkit.getOnlinePlayers();
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        var player = Bukkit.getPlayer(playerId);
        return player == null ? Audience.empty() : player;
    }

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return sender;
    }

    @Override
    public Sound.@NotNull Emitter asEmitter(@NotNull Entity entity) {
        return entity;
    }

    @Override
    public @NotNull Audience world(@NotNull Key worldKey) {
        var world = Bukkit.getWorld(new NamespacedKey(worldKey.namespace(), worldKey.value()));
        return world == null ? Audience.empty() : world;
    }
}
