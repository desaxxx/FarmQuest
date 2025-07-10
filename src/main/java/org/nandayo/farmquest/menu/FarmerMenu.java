package org.nandayo.farmquest.menu;

import org.bukkit.Material;
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
import org.nandayo.farmquest.model.Farmer;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.service.registry.GUIRegistry;
import org.nandayo.farmquest.util.FUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FarmerMenu extends Menu {

    private final @NotNull FarmQuest plugin;
    private final @NotNull Player player;
    private final @NotNull Farm farm;
    private final @NotNull GUIRegistry guiRegistry;
    public FarmerMenu(@NotNull FarmQuest plugin, @NotNull Player player, @NotNull Farm farm) {
        this.plugin = plugin;
        this.player = player;
        this.farm = farm;
        this.guiRegistry = plugin.guiRegistry;
        open();
    }
    
    private final String menuNamespace = "menus.farmer_menu";

    private void open() {
        Farmer farmer = Farmer.getPlayer(player);
        if(farmer == null) {
            plugin.tell(player, plugin.languageUtil.getString("not_a_farmer_player"));
            return;
        }

        createInventory(MenuType.CHEST_1_ROW, guiRegistry.getString(menuNamespace + ".title").replace("{farm}", farm.getId()));

        // Pickup quest
        addButton(new Button() {
            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(3);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.WRITABLE_BOOK)
                        .name(guiRegistry.getString(menuNamespace + ".pickup.name"))
                        .lore(guiRegistry.getStringList(menuNamespace + ".pickup.lore"))
                        .get();
            }

            @Override
            public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                new FarmerMenuQuestList(plugin, player, 1, farm, farmer);
            }
        });

        // Delivery
        addButton(new Button() {
            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(5);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.BARREL)
                        .name(guiRegistry.getString(menuNamespace + ".delivery.name"))
                        .lore(guiRegistry.getStringList(menuNamespace + ".delivery.lore"))
                        .get();
            }

            @Override
            public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                player.performCommand("farmquest deliver " + farm.getId());
            }
        });

        // Drop quest
        addButton(new Button() {
            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(8);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.REDSTONE)
                        .name(guiRegistry.getString(menuNamespace + ".drop.name"))
                        .lore(guiRegistry.getStringList(menuNamespace + ".drop.lore"))
                        .get();
            }

            @Override
            public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                if(farmer.getActiveQuestProgress() == null) {
                    farmer.tell(plugin.languageUtil.getString("do_not_have_active_quest"));
                    return;
                }
                farmer.dropQuest(false);
                farmer.tell(plugin.languageUtil.getString("drop_quest"));
            }
        });

        displayTo(player);
    }

    private class FarmerMenuQuestList extends Menu {

        private final @NotNull FarmQuest plugin;
        private final @NotNull Player player;
        private final int page;
        private final @NotNull Farm farm;
        private final @NotNull Farmer farmer;
        public FarmerMenuQuestList(@NotNull FarmQuest plugin, @NotNull Player player, int page, @NotNull Farm farm, @NotNull Farmer farmer) {
            this.plugin = plugin;
            this.player = player;
            this.page = page;
            this.farm = farm;
            this.farmer = farmer;
            open();
        }

        private final List<Integer> availableSlots = Arrays.asList(FUtil.getIntegersBetween(0,53, 45,53).toArray(new Integer[0]));

        private void open() {
            createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".sub_quest_list.title").replace("{farm}", farm.getId()));

            int itemsPerPage = availableSlots.size();
            int questIndex = (page-1) * itemsPerPage;
            List<Quest> quests = (List<Quest>) farm.getQuests();
            for(int i = 0; i < itemsPerPage; i++) {
                if(questIndex >= quests.size()) {
                    break;
                }
                Quest quest = quests.get(questIndex);
                int slot = availableSlots.get(questIndex % itemsPerPage);
                questIndex++;

                addButton(new Button() {
                    @Override
                    public @NotNull Set<Integer> getSlots() {
                        return Set.of(slot);
                    }

                    @Override
                    public ItemStack getItem() {
                        return ItemCreator.of(quest.getIcon())
                                .name(guiRegistry.getString(menuNamespace + ".sub_quest_list.quest.name").replace("{quest}", quest.getName()))
                                .lore(() -> {
                                    List<String> rawLore = guiRegistry.getStringList(menuNamespace + ".sub_quest_list.quest.lore");
                                    List<String> lore = new ArrayList<>();
                                    for(String line : rawLore) {
                                        lore.add(line.replace("{quest_description}", quest.getDescription()));
                                    }
                                    return lore;
                                })
                                .get();
                    }

                    @Override
                    public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                        player.closeInventory();
                        farmer.pickupQuest(quest, farm);
                    }
                });
            }

            boolean prevPageAvailable = page > 1;
            addButton(new Button() {
                @Override
                public @NotNull Set<Integer> getSlots() {
                    return Set.of(45);
                }

                @Override
                public ItemStack getItem() {
                    if(prevPageAvailable) {
                        return ItemCreator.of(Material.ARROW).name("&cPrevious Page").get();
                    }else {
                        return ItemCreator.of(Material.RED_DYE).name(" ").get();
                    }
                }

                @Override
                public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                    if(prevPageAvailable) {
                        new FarmerMenuQuestList(plugin, player, page-1 ,farm, farmer);
                    }
                }
            });

            boolean nextPageAvailable = page < (quests.size()-1) / itemsPerPage + 1;
            addButton(new Button() {
                @Override
                public @NotNull Set<Integer> getSlots() {
                    return Set.of(53);
                }

                @Override
                public ItemStack getItem() {
                    if(nextPageAvailable) {
                        return ItemCreator.of(Material.ARROW).name("&aNext Page").get();
                    }else {
                        return ItemCreator.of(Material.RED_DYE).name(" ").get();
                    }
                }

                @Override
                public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                    if(nextPageAvailable) {
                        new FarmerMenuQuestList(plugin, player, page+1 ,farm, farmer);
                    }
                }
            });

            displayTo(player);
        }
    }
}
