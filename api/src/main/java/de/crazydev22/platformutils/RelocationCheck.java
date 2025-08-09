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
import net.kyori.adventure.text.Component;

import java.util.*;

class RelocationCheck {
    private static final Set<String> CLASS_NAMES = classNames(
            "net#kyori#adventure#audience#Audience",
            "net#kyori#adventure#text#Component"
    );

    public static boolean isRelocated() {
        return isRelocated(Audience.class) || isRelocated(Component.class);
    }

    public static boolean isRelocated(Class<?> clazz) {
        return !CLASS_NAMES.contains(clazz.getCanonicalName());
    }

    private static Set<String> classNames(String... classes) {
        Set<String> set = new HashSet<>(classes.length);
        for (var className : classes) {
            set.add(className.replace('.', '#'));
        }
        return Collections.unmodifiableSet(set);
    }
}
