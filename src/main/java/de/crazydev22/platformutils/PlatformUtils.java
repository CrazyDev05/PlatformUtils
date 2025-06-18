package de.crazydev22.platformutils;

import de.crazydev22.platformutils.paper.PaperPlatform;
import de.crazydev22.platformutils.spigot.SpigotPlatform;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for detecting and creating platform-specific instances based on the server configuration.
 * This class provides methods to initialize the appropriate platform implementation.
 * <p>
 * It is designed to be non-instantiable and only provides static utility methods.
 */
public class PlatformUtils {
    private PlatformUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates and returns a platform instance based on the currently available server configuration.
     *
     * @param plugin the plugin instance used to initialize the platform
     * @return an appropriate Platform implementation based on the detected server configuration
     * @throws IllegalStateException if the platform is unsupported
     */
    @NotNull
    public static Platform createPlatform(@NotNull Plugin plugin) {
        if (hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration"))
            return new PaperPlatform(plugin);
        if (hasClass("org.spigotmc.SpigotConfig"))
            return new SpigotPlatform(plugin);
        throw new IllegalStateException("Unsupported platform!");
    }

    @ApiStatus.Internal
    private static boolean hasClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
