package de.crazydev22.platformutils.spigot;

import de.crazydev22.platformutils.ItemEditor;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.crazydev22.platformutils.spigot.SpigotUtils.*;

public class SpigotItemEditor implements ItemEditor {
    private final ItemStack stack;
    private final ItemMeta meta;
    private final BookMeta bookMeta;

    public SpigotItemEditor(ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();
        this.bookMeta = meta instanceof BookMeta ? (BookMeta) meta : null;
    }

    @Override
    public @Nullable Component displayName() {
        return deserialize(meta.getDisplayName());
    }

    @Override
    public ItemEditor displayName(@Nullable Component displayName) {
        meta.setDisplayName(serialize(displayName));
        return this;
    }

    @Override
    public @Nullable Component customName() {
        return displayName();
    }

    @Override
    public ItemEditor customName(@Nullable Component customName) {
        return displayName(customName);
    }

    @Override
    public @Nullable List<@NotNull Component> lore() {
        return deserialize(meta.getLore());
    }

    @Override
    public ItemEditor lore(@Nullable List<@NotNull Component> lore) {
        meta.setLore(serialize(lore));
        return this;
    }

    @Override
    public @Nullable Component author() {
        if (bookMeta == null) return null;
        return deserialize(bookMeta.getAuthor());
    }

    @Override
    public ItemEditor author(@Nullable Component author) {
        if (bookMeta == null) return this;
        bookMeta.setAuthor(serialize(author));
        return this;
    }

    @Override
    public @Nullable Component title() {
        if (bookMeta == null) return null;
        return deserialize(bookMeta.getTitle());
    }

    @Override
    public ItemEditor title(@Nullable Component title) {
        if (bookMeta == null) return this;
        bookMeta.setTitle(serialize(title));
        return this;
    }

    @Override
    public @NotNull List<@NotNull Component> pages() {
        if (bookMeta == null) return List.of();
        return bookMeta.spigot()
                .getPages()
                .stream()
                .map(ComponentSerializer::toString)
                .map(SpigotUtils::deserializeGson)
                .toList();
    }

    @Override
    public ItemEditor pages(@NotNull List<@NotNull Component> pages) {
        if (bookMeta == null) return this;
        bookMeta.spigot().setPages(pages.stream()
                .map(SpigotUtils::serializeGson)
                .map(ComponentSerializer::parse)
                .toList());
        return this;
    }

    @Override
    public ItemEditor addPages(@NotNull List<@NotNull Component> pages) {
        if (bookMeta == null) return this;
        for (var page : pages) {
            bookMeta.spigot().addPage(ComponentSerializer.parse(SpigotUtils.serializeGson(page)));
        }
        return this;
    }

    @Override
    public ItemEditor setPage(int page, @NotNull Component content) {
        if (bookMeta == null) return this;
        bookMeta.spigot().setPage(page, ComponentSerializer.parse(SpigotUtils.serializeGson(content)));
        return this;
    }

    @Override
    public @NotNull ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }
}
