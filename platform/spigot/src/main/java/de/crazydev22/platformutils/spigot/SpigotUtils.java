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
