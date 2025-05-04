package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.Button;
import org.nandayo.dapi.guimanager.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.ArrayList;
import java.util.List;

public class FarmerMenu extends Menu {

    private final FarmQuest plugin;
    private final Farm farm;
    public FarmerMenu(@NotNull FarmQuest plugin, @NotNull Farm farm) {
        this.plugin = plugin;
        this.farm = farm;
    }

    public void open(@NotNull Player player) {
        Farmer farmer = Farmer.getPlayer(player);
        if(farmer == null) {
            plugin.tell(player, plugin.languageUtil.getString("not_a_farmer_player"));
            return;
        }

        this.createInventory(9, plugin.languageUtil.getString("menu.farmer_menu.title").replace("{farm}", farm.getId()));

        // Pickup quest
        this.addButton(new Button(3) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.WRITABLE_BOOK)
                        .name(plugin.languageUtil.getString("menu.farmer_menu.pickup.name"))
                        .lore(plugin.languageUtil.getStringList("menu.farmer_menu.pickup.lore"))
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {
                new FarmerMenuQuestList(plugin, farm, farmer).open(player);
            }
        });

        // Delivery
        this.addButton(new Button(5) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.BARREL)
                        .name(plugin.languageUtil.getString("menu.farmer_menu.delivery.name"))
                        .lore(plugin.languageUtil.getStringList("menu.farmer_menu.delivery.lore"))
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {
                player.performCommand("farmquest deliver " + farm.getId());
            }
        });

        // Drop quest
        this.addButton(new Button(8) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.REDSTONE)
                        .name(plugin.languageUtil.getString("menu.farmer_menu.drop.name"))
                        .lore(plugin.languageUtil.getStringList("menu.farmer_menu.drop.lore"))
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {
                if(farmer.getActiveQuestProgress() == null) {
                    farmer.tell(plugin.languageUtil.getString("do_not_have_active_quest"));
                    return;
                }
                farmer.dropQuest(false);
                farmer.tell(plugin.languageUtil.getString("drop_quest"));
            }
        });

        this.displayTo(player);
    }

    static private class FarmerMenuQuestList extends Menu {

        private final Farm farm;
        private final Farmer farmer;
        private final FarmQuest plugin;
        public FarmerMenuQuestList(@NotNull FarmQuest plugin, @NotNull Farm farm, @NotNull Farmer farmer) {
            this.plugin = plugin;
            this.farm = farm;
            this.farmer = farmer;
        }

        public void open(@NotNull Player player) {
            this.createInventory(54, plugin.languageUtil.getString("menu.farmer_menu.sub_quest_list.title").replace("{farm}", farm.getId()));

            int slot = 0;
            for(Quest quest : farm.getQuests()) {
                if(slot > 54) break;
                this.addButton(new Button(slot++) {
                    @Override
                    public ItemStack getItem() {
                        return ItemCreator.of(Material.PAPER)
                                .name(plugin.languageUtil.getString("menu.farmer_menu.sub_quest_list.quest.name").replace("{quest}", quest.getName()))
                                .lore(() -> {
                                    List<String> rawLore = plugin.languageUtil.getStringList("menu.farmer_menu.sub_quest_list.quest.lore");
                                    List<String> lore = new ArrayList<>();
                                    for(String line : rawLore) {
                                        if(line.contains("{quest_description}")) {
                                            lore.add(line.replace("{quest_description}", quest.getDescription()));
                                        }else {
                                            lore.add(line);
                                        }
                                    }
                                    return lore;
                                })
                                .get();
                    }

                    @Override
                    public void onClick(Player player, ClickType clickType) {
                        player.closeInventory();
                        farmer.pickupQuest(quest, farm);
                    }
                });
            }

            this.displayTo(player);
        }
    }
}
