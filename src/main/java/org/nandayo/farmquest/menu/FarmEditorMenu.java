package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.MenuType;
import org.nandayo.dapi.guimanager.button.Button;
import org.nandayo.dapi.guimanager.menu.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.service.registry.GUIRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FarmEditorMenu extends Menu {

    private final @NotNull FarmQuest plugin;
    private final @NotNull Player viewer;
    private final @NotNull Farm farm;
    private final @NotNull GUIRegistry guiRegistry;
    public FarmEditorMenu(@NotNull Player viewer, @NotNull Farm farm) {
        this.plugin = FarmQuest.getInstance();
        this.viewer = viewer;
        this.farm = farm;
        this.guiRegistry = plugin.guiRegistry;
        open();
    }

    private final String menuNamespace = "menus.farm_editor";

    private void open() {
        createInventory(MenuType.CHEST_1_ROW, guiRegistry.getString(menuNamespace + ".title").replace("{farm}", farm.getId()));

        // Region
        addButton(new Button() {
            final String path = "region";

            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(0);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.HAY_BLOCK)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name").replace("{region}", farm.getRegion().parseString()))
                        .lore(guiRegistry.getStringList(menuNamespace + "." + path + ".lore").stream().map(l -> l.replace("{region}", farm.getRegion().parseString())).toList())
                        .get();
            }
        });

        // Linked Quests
        this.addButton(new Button() {
            final List<Quest> linkedQuests = farm.getQuests().stream().filter(Objects::nonNull).toList();

            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(1);
            }

            @Override
            public ItemStack getItem() {
                final String path = "linked_quests";

                return ItemCreator.of(Material.WRITABLE_BOOK)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                        .lore(() -> {
                            List<String> rawLore = guiRegistry.getStringList(menuNamespace + "." + path + ".lore");
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
            public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                if(clickType == ClickType.LEFT) {
                    linkQuestViaMenu(farm, player);
                }
                else if(clickType == ClickType.RIGHT) {
                    unlinkQuestViaMenu(farm, player);
                }
            }
        });

        this.displayTo(viewer);
    }


    private void linkQuestViaMenu(@NotNull Farm farm, @NotNull Player player) {
        new QuestListMenu(player, 1,
                (quest) -> {
                    if(quest == null) return;
                    if(farm.linkQuest(quest)) {
                        plugin.farmRegistry.save();
                        plugin.tell(player, plugin.languageUtil.getString("quest_linked_to_farm").replace("{farm}", farm.getId()));
                        player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1f, 1f);
                    } else {
                        plugin.tell(player, plugin.languageUtil.getString("quest_already_linked_to_farm").replace("{farm}", farm.getId()));
                    }
                    new FarmEditorMenu(player, farm);
                });
    }

    private void unlinkQuestViaMenu(@NotNull Farm farm, @NotNull Player player) {
        new QuestListMenu(player, 1,
                (quest) -> {
                    if(quest == null) return;
                    if(farm.unlinkQuest(quest)) {
                        plugin.farmRegistry.save();
                        plugin.tell(player, plugin.languageUtil.getString("quest_unlink_from_farm").replace("{farm}", farm.getId()));
                        player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1f, 1f);
                    }else {
                        plugin.tell(player, plugin.languageUtil.getString("quest_already_not_linked_to_farm").replace("{farm}", farm.getId()));
                    }
                    new FarmEditorMenu(player, farm);
                });
    }
}
