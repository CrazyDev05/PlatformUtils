package de.crazydev22.platformutils;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents an editor for modifying properties of an {@link ItemStack}.
 * This interface provides methods for setting and retrieving various attributes such as display name, lore, custom name,
 * and book-specific properties like author, title, and pages.
 */
public interface ItemEditor {
    /**
     * Retrieves the display name of the item being edited.
     *
     * @return the display name of the item as a {@link Component}, or {@code null} if no display name is set.
     */
    @Nullable Component displayName();

    /**
     * Sets the display name of the item being edited.
     *
     * @param displayName the new display name to set, represented as a {@link Component}, or {@code null} to remove the display name.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor displayName(@Nullable Component displayName);

    /**
     * Retrieves the custom name of the item being edited.
     *
     * @return the custom name of the item as a {@link Component}, or {@code null} if no custom name is set.
     */
    @Nullable Component customName();

    /**
     * Sets the custom name of the item being edited.
     *
     * @param customName the new custom name to set, represented as a {@link Component}, or {@code null} to remove the custom name.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor customName(@Nullable Component customName);

    /**
     * Retrieves the lore (description lines) of the item being edited.
     * Each line of the lore is represented as a {@link Component}.
     *
     * @return a {@link List} of {@link Component} representing the item's lore,
     *         or {@code null} if no lore is set. Each element in the list is guaranteed to be non-null.
     */
    @Nullable List<@NotNull Component> lore();

    /**
     * Sets the lore (description lines) of the item being edited.
     * Each line of the lore is represented as a {@link Component}.
     * Passing {@code null} will remove any existing lore from the item.
     *
     * @param lore a {@link List} of {@link Component} objects representing the new item lore,
     *             or {@code null} to remove the lore. Each element in the list must be non-null.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor lore(@Nullable List<@NotNull Component> lore);

    /* Written Books */
    /**
     * Retrieves the author of the item being edited, typically used for signed books.
     *
     * @return the author of the item as a {@link Component}, or {@code null} if no author is set.
     */
    @Nullable Component author();

    /**
     * Sets the author of the item being edited, typically used for signed books.
     *
     * @param author the author to set, represented as a {@link Component},
     *               or {@code null} to remove the author.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor author(@Nullable Component author);

    /**
     * Retrieves the title of the item being edited, typically used for signed books.
     *
     * @return the title of the item as a {@link Component}, or {@code null} if no title is set.
     */
    @Nullable Component title();

    /**
     * Sets the title of the item being edited, typically used for signed books.
     *
     * @param title the title to set, represented as a {@link Component}, or {@code null} to remove the title.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor title(@Nullable Component title);

    /* Writable Books */
    /**
     * Retrieves the pages of the item being edited, typically used for editable books.
     * Each page of the book is represented as a {@link Component}.
     *
     * @return a {@link List} of {@link Component} objects representing the pages of the item.
     *         Each element in the list is guaranteed to be non-null.
     */
    @NotNull List<@NotNull Component> pages();

    /**
     * Sets the pages of the item being edited, typically used for books.
     * Each page of the book is represented as a {@link Component}.
     *
     * @param pages a {@link List} of {@link Component} objects representing the pages of the item.
     *              Each element in the list must be non-null.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor pages(@NotNull List<@NotNull Component> pages);

    /**
     * Adds additional pages to the existing pages of the item being edited.
     * Each page is represented as a {@link Component}.
     *
     * @param pages a {@link List} of {@link Component} objects representing the pages to add.
     *              Each element in the list must be non-null.
     * @return the current {@code ItemEditor} instance, for method chaining.
     */
    @Contract("_ -> this")
    ItemEditor addPages(@NotNull List<@NotNull Component> pages);

    /**
     * Sets the content of a specific page in the item being edited, typically used for books.
     *
     * @param page the index of the page to set, starting at 0. Must be within the bounds of the existing pages.
     * @param content the content to set on the specified page, represented as a {@link Component}. Must not be {@code null}.
     * @return the current {@code ItemEditor} instance, allowing for method chaining.
     */
    @Contract("_, _ -> this")
    ItemEditor setPage(int page, @NotNull Component content);

    /**
     * Constructs and returns a finalized {@link ItemStack} based on the current state of the {@code ItemEditor}.
     * This includes applying any properties such as display name, custom name, lore, author, title, or pages
     * that have been set using the appropriate methods in the editor.
     *
     * @return a non-null {@link ItemStack} instance containing the configured item data.
     */
    @NotNull ItemStack build();
}
