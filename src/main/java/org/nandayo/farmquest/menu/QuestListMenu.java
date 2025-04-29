package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.Button;
import org.nandayo.dapi.guimanager.Menu;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.function.Consumer;

public class QuestListMenu extends Menu {

    public QuestListMenu() {
    }

    public void open(@NotNull Player player, @NotNull Consumer<Quest> consumer) {
        this.createInventory(54, "&8Quest List | Choose One");

        int slot = 0;
        for(Quest quest : Quest.getRegisteredQuests()) {
            this.addButton(new Button(slot++) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.PAPER)
                            .name("{TITLE}Quest {WHITE} '" + quest.getId() + "'")
                            .lore("{TITLE}Name: {WHITE}" + quest.getName(),
                                    "{TITLE}Description: {WHITE}" + quest.getDescription())
                            .get();
                }

                @Override
                public void onClick(Player player, ClickType clickType) {
                    consumer.accept(quest);
                }
            });
        }

        this.displayTo(player);
    }
}
