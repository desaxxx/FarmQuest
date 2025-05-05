package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.Button;
import org.nandayo.dapi.guimanager.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class QuestListMenu extends Menu {

    private final FarmQuest plugin;
    public QuestListMenu(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player player, @NotNull Consumer<@Nullable Quest> consumer) {
        this.createInventory(54, plugin.languageUtil.getString("menu.quest_list.title"));

        int slot = 0;
        for(Quest quest : Quest.getRegisteredQuests()) {
            this.addButton(new Button(slot++) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(Material.PAPER)
                            .name(plugin.languageUtil.getString("menu.quest_list.quest.name").replace("{quest}", quest.getId()))
                            .lore(() -> {
                                List<String> rawLore = plugin.languageUtil.getStringList("menu.quest_list.quest.lore");
                                List<String> lore = new ArrayList<>();
                                for(String line : rawLore) {
                                    String fixedLine = line;
                                    if(line.contains("{quest}")) {
                                        fixedLine = line.replace("{quest}", quest.getId());
                                    }
                                    if(line.contains("{quest_name}")) {
                                        fixedLine = line.replace("{quest_name}", quest.getName());
                                    }
                                    if(line.contains("{quest_description}")) {
                                        fixedLine = line.replace("{quest_description}", quest.getDescription());
                                    }
                                    lore.add(fixedLine);
                                }
                                return lore;
                            })
                            .get();
                }

                @Override
                public void onClick(Player player, ClickType clickType) {
                    consumer.accept(quest);
                }
            });
        }

        this.runOnClose(inv -> consumer.accept(null));

        this.displayTo(player);
    }
}
