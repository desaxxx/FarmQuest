package org.nandayo.farmquest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.guimanager.MenuType;
import org.nandayo.dapi.guimanager.button.Button;
import org.nandayo.dapi.guimanager.menu.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.quest.Quest;
import org.nandayo.farmquest.model.quest.QuestProperty;
import org.nandayo.farmquest.service.registry.GUIRegistry;
import org.nandayo.farmquest.util.FUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class QuestListMenu extends Menu {

    private final @NotNull FarmQuest plugin;
    private final @NotNull Player player;
    private final int page;
    private final @NotNull Consumer<@Nullable Quest> consumer;
    private final @NotNull GUIRegistry guiRegistry;
    public QuestListMenu(@NotNull Player player, int page, @NotNull Consumer<@Nullable Quest> consumer) {
        this.plugin = FarmQuest.getInstance();
        this.player = player;
        this.page = page;
        this.consumer = consumer;
        this.guiRegistry = plugin.guiRegistry;
        open();
    }

    private final String menuNamespace = "menus.quest_list";

    private final List<Integer> availableSlots = Arrays.asList(FUtil.getIntegersBetween(0,53, 45,53).toArray(new Integer[0]));

    private void open() {
        createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".title"));

        int itemsPerPage = availableSlots.size();
        int questIndex = (page-1) * itemsPerPage;
        List<Quest> quests = (List<Quest>) Quest.getRegisteredQuests();
        for(int i = 0; i < itemsPerPage; i++) {
            if(questIndex >= quests.size()) {
                break;
            }
            Quest quest = quests.get(questIndex);
            int slot = availableSlots.get(questIndex % itemsPerPage);
            questIndex++;

            this.addButton(new Button() {
                final String path = "quest";

                @Override
                public @NotNull Set<Integer> getSlots() {
                    return Set.of(slot);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(quest.getIcon())
                            .name(guiRegistry.getString(menuNamespace + "." + path + ".name").replace("{quest}", quest.getId()))
                            .lore(() -> {
                                List<String> lore = new ArrayList<>();
                                for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")) {
                                    if(line.contains("{property_state}") || line.contains("{property_name}")) {
                                        for(QuestProperty.Property property : quest.getQuestProperty().values()) {
                                            lore.add(line.replace("{property_state}", property.isEnabled() ? "ENABLED" : "DISABLED")
                                                    .replace("{property_name}", property.getDisplayName()));
                                        }
                                    }else {
                                        lore.add(line.replace("{quest}", quest.getId())
                                                .replace("{quest_name}", quest.getName())
                                                .replace("{quest_description}", quest.getDescription()));
                                    }
                                }
                                return lore;
                            })
                            .get();
                }

                @Override
                public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                    consumer.accept(quest);
                }
            });
        }

        boolean prevPageAvailable = page > 1;
        this.addButton(new Button() {
            final String path = "previous";

            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(45);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.ARROW)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                        .lore(guiRegistry.getStringList(menuNamespace + "." + path + ".lore"))
                        .get();
            }

            @Override
            public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                if(prevPageAvailable) {
                    new QuestListMenu(player, page-1, consumer);
                }
            }
        });

        boolean nextPageAvailable = page < (quests.size() - 1) / itemsPerPage + 1;
        this.addButton(new Button() {
            final String path = "next";

            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(53);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(Material.ARROW)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                        .lore(guiRegistry.getStringList(menuNamespace + "." + path + ".lore"))
                        .get();
            }

            @Override
            public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
                if(nextPageAvailable) {
                    new QuestListMenu(player, page+1, consumer);
                }
            }
        });

        this.displayTo(player);
    }

    @Override
    public <T extends Inventory> Consumer<T> onClose() {
        return inv -> consumer.accept(null);
    }
}
