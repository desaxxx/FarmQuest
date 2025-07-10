package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.button.Button;
import org.nandayo.dapi.guimanager.menu.AnvilMenu;
import org.nandayo.farmquest.service.OneTimeConsumer;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class AnvilInput extends AnvilMenu {

    private final @NotNull Player player;
    private final @NotNull String title;
    private final @NotNull String initialText;
    private final @NotNull List<String> lore;
    private final @NotNull OneTimeConsumer<String> textConsumer;
    public AnvilInput(@NotNull Player player, @NotNull String title, @NotNull String initialText, @NotNull List<String> lore, @NotNull OneTimeConsumer<String> textConsumer) {
        this.player = player;
        this.title = title;
        this.initialText = initialText.isEmpty() ? " " : initialText;
        this.lore = lore;
        this.textConsumer = textConsumer;
        open();
    }

    private void open() {
        createInventory(player, title);

        addButton(new Button() {
            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(0);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.PAPER)
                        .name(initialText)
                        .get();
            }
        });

        addButton(new Button() {
            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(2);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.NAME_TAG)
                        .name(initialText)
                        .lore(lore)
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                textConsumer.acceptSync(getText());
            }
        });

        displayTo(player);
    }

    @Override
    public <T extends Inventory> Consumer<T> onClose() {
        return inv -> textConsumer.acceptSync(getText());
    }
}
