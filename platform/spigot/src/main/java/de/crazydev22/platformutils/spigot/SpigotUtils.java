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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;

import java.util.List;

class SpigotUtils {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .extractUrls()
            .character('§')
            .build();
    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();


    @Contract("null -> null; !null -> !null")
    static List<Component> deserialize(List<String> raw) {
        if (raw == null) return null;
        return raw.stream().map(SpigotUtils::deserialize).toList();
    }

    @Contract("null -> null; !null -> !null")
    static List<String> serialize(List<Component> raw) {
        if (raw == null) return null;
        return raw.stream().map(SpigotUtils::serialize).toList();
    }

    @Contract("null -> null; !null -> !null")
    static Component deserialize(String raw) {
        if (raw == null) return null;
        return LEGACY.deserialize(raw);
    }

    @Contract("null -> null; !null -> !null")
    static String serialize(Component raw) {
        if (raw == null) return null;
        return LEGACY.serialize(raw);
    }

    @Contract("null -> null; !null -> !null")
    static Component deserializeGson(String raw) {
        if (raw == null) return null;
        return GSON.deserialize(raw);
    }

    @Contract("null -> null; !null -> !null")
    static String serializeGson(Component raw) {
        if (raw == null) return null;
        return GSON.serialize(raw);
    }
}
