package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.Button;
import org.nandayo.dapi.guimanager.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FarmEditorMenu extends Menu {

    private final FarmQuest plugin;
    public FarmEditorMenu(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player viewer, @NotNull Farm farm) {
        this.createInventory(9, plugin.languageUtil.getString("menu.farm_editor.title").replace("{farm}", farm.getId()));

        // Region
        this.addButton(new Button(0) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.HAY_BLOCK)
                        .name(plugin.languageUtil.getString("menu.farm_editor.region.name").replace("{region}", farm.getRegion().parseString()))
                        .lore(plugin.languageUtil.getStringList("menu.farm_editor.region.lore").stream().map(l -> l.replace("{region}", farm.getRegion().parseString())).toList())
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {

            }
        });

        // Linked Quests
        this.addButton(new Button(1) {
            final List<Quest> linkedQuests = farm.getQuests().stream().filter(Objects::nonNull).toList();
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.WRITABLE_BOOK)
                        .name(plugin.languageUtil.getString("menu.farm_editor.linked_quests.name"))
                        .lore("")
                        .addLore(() -> {
                            List<String> rawLore = plugin.languageUtil.getStringList("menu.farm_editor.linked_quests.lore");
                            List<String> lore = new ArrayList<>();
                            for (String line : rawLore) {
                                if (line.contains("{quest.%n}")) {
                                    for (Quest quest : linkedQuests) {
                                        lore.add(line.replace("{quest.%n}", quest.getId()));
                                    }
                                } else {
                                    lore.add(line);
                                }
                            }
                            return lore;
                        })
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {

                // Adding
                if(clickType == ClickType.LEFT) {
                    new QuestListMenu(plugin).open(player,
                            (quest) -> {
                                if(farm.linkQuest(quest)) {
                                    plugin.farmRegistry.save();
                                    plugin.tell(player, plugin.languageUtil.getString("quest_linked_to_farm").replace("{farm}", farm.getId()));
                                        player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1f, 1f);
                                } else {
                                    plugin.tell(player, plugin.languageUtil.getString("quest_already_linked_to_farm").replace("{farm}", farm.getId()));
                                }
                                new FarmEditorMenu(plugin).open(player, farm);
                            });
                }
                // Removing
                else if(clickType == ClickType.RIGHT) {
                    new QuestListMenu(plugin).open(player,
                            (quest) -> {
                                if(farm.unlinkQuest(quest)) {
                                    plugin.farmRegistry.save();
                                    plugin.tell(player, plugin.languageUtil.getString("quest_unlink_from_farm").replace("{farm}", farm.getId()));
                                    player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1f, 1f);
                                }else {
                                    plugin.tell(player, plugin.languageUtil.getString("quest_already_not_linked_to_farm").replace("{farm}", farm.getId()));
                                }
                                new FarmEditorMenu(plugin).open(player, farm);
                            });
                }
            }
        });

        this.displayTo(viewer);
    }
}
