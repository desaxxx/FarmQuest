package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.DAPI.ItemCreator;
import org.nandayo.DAPI.guimanager.Button;
import org.nandayo.DAPI.guimanager.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.Farm;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.List;
import java.util.Objects;

public class FarmEditorMenu extends Menu {

    private final FarmQuest plugin;
    public FarmEditorMenu(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player viewer, @NotNull Farm farm) {
        this.createInventory(9, String.format("&8Farm Editor ({WHITE}%s&8)", farm.getId()));

        // Region
        this.addButton(new Button(0) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.HAY_BLOCK)
                        .name("{TITLE}Region: {WHITE}" + farm.getRegion().parseString())
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {

            }
        });

        // Linked Quests
        this.addButton(new Button(1) {
            final List<String> questLore = farm.getQuests().stream().filter(Objects::nonNull).map(q ->" {STAR}* {WHITE}" + q.getId()).toList();
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.WRITABLE_BOOK)
                        .name("{TITLE}&nLinked Quests")
                        .lore("")
                        .addLore(questLore)
                        .addLore("",
                                "{WHITE}Left click to add a quest.",
                                "{WHITE}Right click to remove a quest.")
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {

                // Adding
                if(clickType == ClickType.LEFT) {
                    new QuestListMenu(plugin).open(player,
                            (quest) -> {
                                if(farm.linkQuest(quest)) {
                                    plugin.tell(player, String.format("{SUCCESS}Quest has been linked to Farm '%s'.", farm.getId()));
                                } else {
                                    plugin.tell(player, String.format("{WARN}Quest is already linked to Farm '%s'.", farm.getId()));
                                }
                                new FarmEditorMenu(plugin).open(player, farm);
                            });
                }
                // Removing
                else if(clickType == ClickType.RIGHT) {
                    new QuestListMenu(plugin).open(player,
                            (quest) -> {
                                if(farm.unlinkQuest(quest)) {
                                    plugin.tell(player, String.format("{SUCCESS}Quest has been unlinked from Farm '%s'.", farm.getId()));
                                }else {
                                    plugin.tell(player, String.format("{WARN}Quest is already not linked to Farm '%s'.", farm.getId()));
                                }
                                new FarmEditorMenu(plugin).open(player, farm);
                            });
                }
            }
        });

        this.displayTo(viewer);
    }
}
