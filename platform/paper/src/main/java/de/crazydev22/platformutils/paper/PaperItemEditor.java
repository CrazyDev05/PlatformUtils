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

import de.crazydev22.platformutils.ItemEditor;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PaperItemEditor implements ItemEditor {
    private static final Function<ItemEditor, @Nullable Component> getCustomName;
    private static final BiConsumer<ItemEditor, @Nullable Component> setCustomName;

    private final ItemStack stack;
    private final ItemMeta meta;
    private final BookMeta bookMeta;

    public PaperItemEditor(ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();
        if (meta instanceof BookMeta) {
            bookMeta = (BookMeta) meta;
        } else bookMeta = null;
    }

    @Override
    public @Nullable Component displayName() {
        return meta.displayName();
    }

    @Override
    public ItemEditor displayName(@Nullable Component displayName) {
        meta.displayName(displayName);
        return this;
    }

    @Override
    public @Nullable Component customName() {
        return getCustomName.apply(this);
    }

    @Override
    public ItemEditor customName(@Nullable Component customName) {
        setCustomName.accept(this, customName);
        return this;
    }

    @Override
    public @Nullable List<@NotNull Component> lore() {
        return meta.lore();
    }

    @Override
    public ItemEditor lore(@Nullable List<@NotNull Component> lore) {
        meta.lore(lore);
        return this;
    }

    @Override
    public @Nullable Component author() {
        if (bookMeta == null) return null;
        return bookMeta.author();
    }

    @Override
    public ItemEditor author(@Nullable Component author) {
        if (bookMeta == null) return this;
        bookMeta.author(author);
        return this;
    }

    @Override
    public @Nullable Component title() {
        if (bookMeta == null) return null;
        return bookMeta.title();
    }

    @Override
    public ItemEditor title(@Nullable Component title) {
        if (bookMeta == null) return this;
        bookMeta.title(title);
        return this;
    }

    @Override
    public @NotNull List<@NotNull Component> pages() {
        if (bookMeta == null) return List.of();
        return bookMeta.pages();
    }

    @Override
    public ItemEditor pages(@NotNull List<@NotNull Component> pages) {
        if (bookMeta == null) return this;
        bookMeta.pages(pages);
        return this;
    }

    @Override
    public ItemEditor addPages(@NotNull List<@NotNull Component> pages) {
        if (bookMeta == null) return this;
        bookMeta.addPages(pages.toArray(Component[]::new));
        return this;
    }

    @Override
    public ItemEditor setPage(int page, @NotNull Component content) {
        if (bookMeta == null) return this;
        bookMeta.page(page, content);
        return this;
    }

    @Override
    public @NotNull ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }

    static {
        Function<ItemEditor, @Nullable Component> get = ItemEditor::displayName;
        BiConsumer<ItemEditor, @Nullable Component> set = ItemEditor::displayName;

        try {
            var getter = findMethod(ItemMeta.class, "customName", Component.class);
            var setter = findMethod(ItemMeta.class, "customName", void.class, Component.class);

            get = editor -> {
                try {
                    return (Component) getter.invoke(editor);
                } catch (Throwable ignored) {
                    return editor.displayName();
                }
            };
            set = (editor, name) -> {
                try {
                    setter.invoke(editor, name);
                } catch (Throwable ignored) {
                    editor.displayName(name);
                }
            };
        } catch (ReflectiveOperationException ignored) {
        }

        getCustomName = get;
        setCustomName = set;
    }

    private static MethodHandle findMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... params) throws ReflectiveOperationException {
        return MethodHandles.publicLookup().findVirtual(clazz, name, MethodType.methodType(returnType, params));
    }
}
