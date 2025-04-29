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
            plugin.tell(player, "{WARN}You are not a farm player.");
            return;
        }

        this.createInventory(9, "&8Farmer Menu");

        // Pickup quest
        this.addButton(new Button(3) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.WRITABLE_BOOK)
                        .name("{TITLE}Pickup a Quest")
                        .lore("{WHITE}Click to see quests!")
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {
                new FarmerMenuQuestList(farm, farmer).open(player);
            }
        });

        // Delivery
        this.addButton(new Button(5) {
            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.BARREL)
                        .name("{TITLE}Deliver")
                        .lore("{WHITE}Click here to deliver materials!")
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
                        .name("{TITLE}Drop the Quest")
                        .lore("&cClick to drop!")
                        .get();
            }

            @Override
            public void onClick(Player player, ClickType clickType) {
                if(farmer.getActiveQuestProgress() == null) {
                    farmer.tell("{WARN}You don't have an active quest.");
                    return;
                }
                farmer.dropQuest(false);
                farmer.tell("{WHITE}Dropped the quest.");
            }
        });

        this.displayTo(player);
    }

    static private class FarmerMenuQuestList extends Menu {

        private final Farm farm;
        private final Farmer farmer;
        public FarmerMenuQuestList(@NotNull Farm farm, @NotNull Farmer farmer) {
            this.farm = farm;
            this.farmer = farmer;
        }

        public void open(@NotNull Player player) {
            this.createInventory(54, "&8Quest List");

            int slot = 0;
            for(Quest quest : farm.getQuests()) {
                if(slot > 54) break;
                this.addButton(new Button(slot++) {
                    @Override
                    public ItemStack getItem() {
                        return ItemCreator.of(Material.PAPER)
                                .name("{TITLE}Quest {WHITE} '" + quest.getName() + "'")
                                .lore(" {STAR}* &7" + quest.getDescription(),
                                        "",
                                        "{WHITE}Click to pickup!")
                                .get();
                    }

                    @Override
                    public void onClick(Player player, ClickType clickType) {
                        player.closeInventory();
                        farmer.pickupQuest(quest);
                    }
                });
            }

            this.displayTo(player);
        }
    }
}
