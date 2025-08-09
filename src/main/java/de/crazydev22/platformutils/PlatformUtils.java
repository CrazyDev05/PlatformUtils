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
     * @throws IllegalStateException if the adventure api was relocated
     */
    @NotNull
    public static Platform createPlatform(@NotNull Plugin plugin) {
        if (RelocationCheck.isRelocated())
            throw new IllegalStateException("Adventure API was relocated.");
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
