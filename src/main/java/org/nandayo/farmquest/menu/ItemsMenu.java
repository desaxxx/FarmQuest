package org.nandayo.farmquest.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.Button;
import org.nandayo.dapi.guimanager.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.farm.FarmTool;

public class ItemsMenu extends Menu {

    private final FarmQuest plugin;
    public ItemsMenu(@NotNull FarmQuest plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player player) {
        this.createInventory(27, plugin.languageUtil.getString("menu.items.title"));

        int slot = 0;
        for(FarmTool tool : FarmTool.getRegisteredTools()) {
            this.addButton(new Button(slot++) {
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(tool.getItem().clone())
                            .addLore(plugin.languageUtil.getStringList("menu.items.tool_extra_lore"))
                            .get();
                }

                @Override
                public void onClick(Player player, ClickType clickType) {
                    player.closeInventory();
                    player.getInventory().addItem(tool.getItem());
                    plugin.tell(player, plugin.languageUtil.getString("get_farm_tool_from_menu").replace("{tool}", tool.getName()));
                }
            });
        }

        this.displayTo(player);
    }
}
