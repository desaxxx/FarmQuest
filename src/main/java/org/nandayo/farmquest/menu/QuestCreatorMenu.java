package org.nandayo.farmquest.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.dapi.ItemCreator;
import org.nandayo.dapi.Util;
import org.nandayo.dapi.guimanager.MenuType;
import org.nandayo.dapi.guimanager.button.Button;
import org.nandayo.dapi.guimanager.button.PatternButton;
import org.nandayo.dapi.guimanager.menu.Menu;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.enumeration.FarmBlock;
import org.nandayo.farmquest.model.quest.*;
import org.nandayo.farmquest.service.OneTimeConsumer;
import org.nandayo.farmquest.service.registry.GUIRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QuestCreatorMenu extends Menu {

    static final private @NotNull List<String> anvilLore = List.of("{WHITE}Click to continue!");

    private final @NotNull FarmQuest plugin;
    private final @NotNull Player player;
    private final @NotNull WritableQuest writableQuest;
    private final @NotNull GUIRegistry guiRegistry;
    private final boolean questAlreadyExists;
    public QuestCreatorMenu(@NotNull Player player, @NotNull String id) {
        this.plugin = FarmQuest.getInstance();
        this.player = player;
        this.writableQuest = new WritableQuest(id);
        this.guiRegistry = FarmQuest.getInstance().guiRegistry;
        this.questAlreadyExists = Quest.getQuest(id) != null;
        open();
    }

    public QuestCreatorMenu(@NotNull Player player, @NotNull WritableQuest writableQuest) {
        this.plugin = FarmQuest.getInstance();
        this.player = player;
        this.writableQuest = writableQuest;
        this.guiRegistry = FarmQuest.getInstance().guiRegistry;
        this.questAlreadyExists = Quest.getQuest(writableQuest.getId()) != null;
        open();
    }

    public String menuNamespace = "menus.quest_creator";
    static private final String[] glassPattern = new String[]{
            "XXXXXXXXX",
            "X       X",
            "X       X",
            "X       X",
            "X       X",
            "XXXXXXXXX"
    };

    private void open() {
        createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".title").replace("{id}", writableQuest.getId()));

        // GLASSES
        addButton(new PatternButton() {
            @Override
            public @NotNull String[] getLayout() {
                return glassPattern;
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").get();
            }
        });

        // OBJECTIVE TYPE
        addButton(new Button() {
            final String path = "objective_type";

            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(20);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(writableQuest.getType().getIcon())
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                .replace("{objective_type}", writableQuest.getType().getDisplayName())
                        )
                        .lore(() -> {
                            List<String> lore = guiRegistry.getStringList(menuNamespace + "." + path + ".lore");
                            lore.replaceAll(l -> l.replace("{objective_type}", writableQuest.getType().getDisplayName()));
                            return lore;
                        })
                        .hideFlag(ItemFlag.values())
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                new ObjectiveTypeSelector(player, new OneTimeConsumer<>(objType -> {
                    if (objType != null) {
                        writableQuest.setType(objType);
                    }
                    new QuestCreatorMenu(player, writableQuest);
                }));
            }
        });

        // FARM BLOCK & TARGET AMOUNT & ICON
        addButton(new Button() {
            final String path = "farm_block";

            @Override
            public @NotNull Set<Integer> getSlots() {
                return Set.of(21);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(writableQuest.getIcon())
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                .replace("{farm_block}", writableQuest.getFarmBlock().name())
                                .replace("{target_amount}", String.valueOf(writableQuest.getTargetAmount()))
                        )
                        .lore(() -> {
                            List<String> lore = guiRegistry.getStringList(menuNamespace + "." + path + ".lore");
                            lore.replaceAll(l -> l.replace("{farm_block}", writableQuest.getFarmBlock().name())
                                    .replace("{target_amount}", String.valueOf(writableQuest.getTargetAmount())));
                            return lore;
                        })
                        .amount(writableQuest.getTargetAmount())
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                if(clickType.isLeftClick()) {
                    new FarmBlockSelector(player, new OneTimeConsumer<>(farmBlock -> {
                        if(farmBlock != null) {
                            writableQuest.setFarmBlock(farmBlock);
                        }
                        new QuestCreatorMenu(player, writableQuest);
                    }));
                }
                else if (clickType == ClickType.RIGHT) {
                    new AnvilInput(player, guiRegistry.getString(menuNamespace + "." + path + ".anvil_title"), String.valueOf(writableQuest.getTargetAmount()), anvilLore,
                            new OneTimeConsumer<>(text -> {
                                if(text != null) {
                                    int amount = writableQuest.getTargetAmount();
                                    try {
                                        amount = Integer.parseInt(text);
                                    }catch (Exception ignored) {}
                                    writableQuest.setTargetAmount(amount);
                                }
                                new QuestCreatorMenu(player, writableQuest);
                            }));
                }else if (clickType == ClickType.SHIFT_RIGHT) {
                    writableQuest.setIcon(null);
                    new QuestCreatorMenu(player, writableQuest);
                }
            }
        });

        // TIME LIMIT
        addButton(new Button() {
            final String path = "time_limit";

            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(22);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.CLOCK)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                .replace("{time_limit}", String.valueOf(writableQuest.getTimeLimit())))
                        .lore(() -> {
                            List<String> lore = guiRegistry.getStringList(menuNamespace + "." + path + ".lore");
                            lore.replaceAll(l -> l.replace("{time_limit}", String.valueOf(writableQuest.getTimeLimit())));
                            return lore;
                        })
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                new AnvilInput(player, guiRegistry.getString(menuNamespace + "." + path + ".anvil_title"), String.valueOf(writableQuest.getTimeLimit()), anvilLore,
                        new OneTimeConsumer<>(text -> {
                            if(text != null) {
                                long timeLimit = writableQuest.getTimeLimit();
                                try {
                                    timeLimit = Long.parseLong(text);
                                }catch (Exception ignored) {}
                                writableQuest.setTimeLimit(timeLimit);
                            }
                            new QuestCreatorMenu(player, writableQuest);
                        }));
            }
        });

        // REWARDS
        addButton(new Button() {
            final String path = "rewards";

            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(23);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.EMERALD)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                        .lore(() -> {
                            List<String> lore = new ArrayList<>();
                            for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")) {
                                if(line.contains("{reward_line}")) {
                                    for(Reward.RewardType type : Reward.RewardType.values()) {
                                        Reward reward = writableQuest.getReward(type);
                                        if(reward == null) continue;
                                        for(String rewardLine : reward.getRun()) {
                                            lore.add(line.replace("{reward_line}", rewardLine));
                                        }
                                    }
                                }else {
                                    lore.add(line);
                                }
                            }
                            return lore;
                        })
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                if(clickType.isLeftClick()) {
                    Reward commandReward = writableQuest.getRewardOrCreate(Reward.RewardType.COMMAND);
                    new RewardManager(player, commandReward.getRun(), new OneTimeConsumer<>(run -> {
                        commandReward.setRun(run);
                        new QuestCreatorMenu(player, writableQuest);

                    }));
                } else if (clickType.isRightClick()) {
                    Reward toolReward = writableQuest.getRewardOrCreate(Reward.RewardType.FARM_TOOL);
                    new RewardManager(player, toolReward.getRun(), new OneTimeConsumer<>(run -> {
                        toolReward.setRun(run);
                        new QuestCreatorMenu(player, writableQuest);
                    }));
                }
            }
        });


        // PROPERTY
        addButton(new Button() {
            final String path = "properties";

            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(24);
            }

            @Override
            public @Nullable ItemStack getItem() {
                QuestProperty questProperty = writableQuest.getQuestProperty();
                return ItemCreator.of(Material.ARMOR_STAND)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                        .lore(() -> {
                            List<String> lore = new ArrayList<>();
                            for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")) {
                                if(line.contains("{property_name}") || line.contains("{property_state}")) {
                                    for(QuestProperty.Property property : questProperty.values()) {
                                        lore.add(line.replace("{property_name}", property.getDisplayName())
                                                .replace("{property_state}", property.isEnabled() ? "ENABLED" : "DISABLED"));
                                    }
                                }else {
                                    lore.add(line);
                                }
                            }
                            return lore;
                        })
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                new PropertyEditor(player, writableQuest.getQuestProperty(), () ->
                        new QuestCreatorMenu(player, writableQuest));
            }
        });


        // NAME
        addButton(new Button() {
            final String path = "name";

            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(30);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.NAME_TAG)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                .replace("{name}", writableQuest.getName()))
                        .lore(() -> {
                            List<String> lore = new ArrayList<>();
                            for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")){
                                lore.add(line.replace("{name}", writableQuest.getName()));
                            }
                            return lore;
                        })
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                new AnvilInput(player, guiRegistry.getString(menuNamespace + "." + path + ".anvil_title"), writableQuest.getName(), anvilLore,
                        new OneTimeConsumer<>(text -> {
                            if(text == null) return;
                            writableQuest.setName(text);
                            new QuestCreatorMenu(player, writableQuest);
                        }));
            }
        });

        // DESCRIPTION
        addButton(new Button() {
            final String path = "description";

            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(32);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.OAK_SIGN)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                .replace("{description}", writableQuest.getDescription()))
                        .lore(() -> {
                            List<String> lore = new ArrayList<>();
                            for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")){
                                lore.add(line.replace("{description}", writableQuest.getDescription()));
                            }
                            return lore;
                        })
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                new AnvilInput(player, guiRegistry.getString(menuNamespace + "." + path + ".anvil_title"), writableQuest.getDescription(), anvilLore,
                        new OneTimeConsumer<>(text -> {
                            if(text == null) return;
                            writableQuest.setDescription(text);
                            new QuestCreatorMenu(player, writableQuest);
                        }));
            }
        });


        // SAVE
        addButton(new Button() {
            final String path = "save";

            @Override
            protected @NotNull Set<Integer> getSlots() {
                return Set.of(53);
            }

            @Override
            public @Nullable ItemStack getItem() {
                return ItemCreator.of(Material.ORANGE_DYE)
                        .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                        .lore(guiRegistry.getStringList(menuNamespace + "." + path + ".lore"))
                        .get();
            }

            @Override
            public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                player.closeInventory();
                Quest quest = questAlreadyExists ? writableQuest.saveToQuest() : writableQuest.createQuest();
                if(quest != null) {
                    quest.register();
                    Util.tell(player, plugin.languageUtil.getString("quest_created"));
                }else {
                    Util.tell(player, plugin.languageUtil.getString("quest_not_created"));
                }
            }
        });


        displayTo(player);
    }

    @Override
    public BiConsumer<PlayerInventory, Integer> onPlayerInventoryClick() {
        return (playerInv, slot) -> {
            ItemStack item = playerInv.getItem(slot);
            if(item == null) return;
            writableQuest.setIcon(item.getType());
            new QuestCreatorMenu(player, writableQuest);
        };
    }







    private class ObjectiveTypeSelector extends Menu {

        private final @NotNull Player player;
        private final @NotNull OneTimeConsumer<Objective.@Nullable ObjectiveType> consumer;
        public ObjectiveTypeSelector(@NotNull Player player, @NotNull OneTimeConsumer<Objective.@Nullable ObjectiveType> consumer) {
            this.player = player;
            this.consumer = consumer;
            open();
        }

        private void open() {
            menuNamespace = "menus.objective_type_selector";
            createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".title"));

            // GLASSES
            addButton(new PatternButton() {
                @Override
                public @NotNull String[] getLayout() {
                    return glassPattern;
                }

                @Override
                public @Nullable ItemStack getItem() {
                    return ItemCreator.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").get();
                }
            });

            int i = 0;
            int[] slots = new int[]{20,31,24};
            for(Objective.ObjectiveType type : Objective.ObjectiveType.values()) {
                int slot = slots[i++];

                addButton(new Button() {
                    final String path = "objective_type";

                    @Override
                    public @NotNull Set<Integer> getSlots() {
                        return Set.of(slot);
                    }

                    @Override
                    public @Nullable ItemStack getItem() {
                        return ItemCreator.of(type.getIcon())
                                .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                        .replace("{objective_type}", type.getDisplayName()))
                                .lore(() -> {
                                    List<String> lore = new ArrayList<>();
                                    for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")){
                                        lore.add(line.replace("{objective_type}", type.getDisplayName()));
                                    }
                                    return lore;
                                })
                                .hideFlag(ItemFlag.values())
                                .get();
                    }

                    @Override
                    public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                        consumer.acceptSync(type);
                    }
                });
            }

            displayTo(player);
        }

        @Override
        public <T extends Inventory> Consumer<T> onClose() {
            return inv -> consumer.acceptSync(null);
        }
    }



    private class FarmBlockSelector extends Menu {

        private final @NotNull Player player;
        private final @NotNull OneTimeConsumer<@Nullable FarmBlock> consumer;
        public FarmBlockSelector(@NotNull Player player, @NotNull OneTimeConsumer<@Nullable FarmBlock> consumer) {
            this.player = player;
            this.consumer = consumer;
            open();
        }

        private void open() {
            menuNamespace = "menus.farm_block_selector";
            createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".title"));

            // GLASSES
            addButton(new PatternButton() {
                @Override
                public @NotNull String[] getLayout() {
                    return glassPattern;
                }

                @Override
                public @Nullable ItemStack getItem() {
                    return ItemCreator.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").get();
                }
            });

            int i = 0;
            int[] slots = new int[]{
                    10,11,12,13,14,15,16,
                    19,20,21,22,23,24,25,
                    28,29,30,31,32,33,34
            };
            for(FarmBlock farmBlock : FarmBlock.values()) {
                int slot = slots[i++];

                addButton(new Button() {
                    final String path = "farm_block";

                    @Override
                    public @NotNull Set<Integer> getSlots() {
                        return Set.of(slot);
                    }

                    @Override
                    public @Nullable ItemStack getItem() {
                        return ItemCreator.of(farmBlock.getSeedMaterial())
                                .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                        .replace("{farm_block}", farmBlock.name()))
                                .lore(() -> {
                                    List<String> lore = new ArrayList<>();
                                    for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")){
                                        lore.add(line.replace("{farm_block}", farmBlock.name()));
                                    }
                                    return lore;
                                })
                                .get();
                    }

                    @Override
                    public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                        consumer.acceptSync(farmBlock);
                    }
                });
            }

            displayTo(player);
        }

        @Override
        public <T extends Inventory> Consumer<T> onClose() {
            return inv -> consumer.acceptSync(null);
        }
    }


    private class RewardManager extends Menu {

        private final @NotNull Player player;
        private final @NotNull List<String> run;
        private final @NotNull OneTimeConsumer<List<String>> consumer;
        public RewardManager(@NotNull Player player, @NotNull List<String> currentRun, @NotNull OneTimeConsumer<List<String>> consumer) {
            this.player = player;
            this.run = currentRun;
            this.consumer = consumer;
            open();
        }

        private void open() {
            menuNamespace = "menus.reward_manager";
            createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".title"));

            int i = 0;
            for(String runLine : new ArrayList<>(run)) {
                int slot = i++;

                addButton(new Button() {
                    final String path = "reward_line";

                    @Override
                    protected @NotNull Set<Integer> getSlots() {
                        return Set.of(slot);
                    }

                    @Override
                    public @Nullable ItemStack getItem() {
                        return ItemCreator.of(Material.PAPER)
                                .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                        .replace("{order}", String.valueOf(slot+1)))
                                .lore(() -> {
                                    List<String> lore = new ArrayList<>();
                                    for(String line : guiRegistry.getStringList(menuNamespace + "." + path + ".lore")){
                                        if(line.contains("{reward_line}")) {
                                            for(String rewardLine : run) {
                                                lore.add(line.replace("{reward_line}", rewardLine));
                                            }
                                        }else {
                                            lore.add(line);
                                        }
                                    }
                                    return lore;
                                })
                                .get();
                    }

                    @Override
                    public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                        if(clickType == ClickType.SHIFT_RIGHT) {
                            run.remove(runLine);
                            new RewardManager(player, run, consumer);
                        }
                    }
                });
            }

            int addSlot = i;
            addButton(new Button() {
                final String path = "new";

                @Override
                protected @NotNull Set<Integer> getSlots() {
                    return Set.of(addSlot);
                }

                @Override
                public @Nullable ItemStack getItem() {
                    return ItemCreator.of(Material.LIME_DYE)
                            .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                            .get();
                }

                @Override
                public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                    new AnvilInput(player, guiRegistry.getString(menuNamespace + "." + path + ".anvil_title"), "", anvilLore,
                            new OneTimeConsumer<>(text -> {
                                if(text == null) return;
                                run.add(text);
                                new RewardManager(player, run, consumer);
                            }));
                }
            });

            addButton(new Button() {
                final String path = "back";

                @Override
                protected @NotNull Set<Integer> getSlots() {
                    return Set.of(53);
                }

                @Override
                public @Nullable ItemStack getItem() {
                    return ItemCreator.of(Material.OAK_DOOR)
                            .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                            .lore(guiRegistry.getStringList(menuNamespace + "." + path + ".lore"))
                            .get();
                }

                @Override
                public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                    consumer.acceptSync(run);
                }
            });


            displayTo(player);
        }
    }


    private class PropertyEditor extends Menu {

        private final @NotNull Player player;
        private final @NotNull QuestProperty questProperty;
        private final @NotNull Runnable runnable;
        public PropertyEditor(@NotNull Player player, @NotNull QuestProperty questProperty, @NotNull Runnable runnable) {
            this.player = player;
            this.questProperty = questProperty;
            this.runnable = runnable;
            open();
        }

        private void open() {
            menuNamespace = "menus.property_editor";
            createInventory(MenuType.CHEST_6_ROWS, guiRegistry.getString(menuNamespace + ".title"));

            // GLASSES
            addButton(new PatternButton() {
                @Override
                public @NotNull String[] getLayout() {
                    return glassPattern;
                }

                @Override
                public @Nullable ItemStack getItem() {
                    return ItemCreator.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").get();
                }
            });

            int starterSlot = 19;
            for(QuestProperty.Property property : questProperty.values()) {
                starterSlot += 2;
                int slot = starterSlot;

                addButton(new Button() {
                    final String path = "property";

                    @Override
                    protected @NotNull Set<Integer> getSlots() {
                        return Set.of(slot);
                    }

                    @Override
                    public @Nullable ItemStack getItem() {
                        return ItemCreator.of(Material.ARMOR_STAND)
                                .name(guiRegistry.getString(menuNamespace + "." + path + ".name")
                                        .replace("{property_state}", property.isEnabled() ? "ENABLED" : "DISABLED")
                                        .replace("{property_name}", property.getDisplayName()))
                                .lore(() -> {
                                    List<String> lore = guiRegistry.getStringList(menuNamespace + "." + path + ".lore");
                                    lore.replaceAll(l -> l.replace("{property_state}", property.isEnabled() ? "ENABLED" : "DISABLED")
                                            .replace("{property_name}", property.getDisplayName()));
                                    return lore;
                                })
                                .get();
                    }

                    @Override
                    public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                        property.setEnabled(!property.isEnabled());
                        new PropertyEditor(player, questProperty, runnable);
                    }
                });
            }

            addButton(new Button() {
                final String path = "back";

                @Override
                protected @NotNull Set<Integer> getSlots() {
                    return Set.of(53);
                }

                @Override
                public @Nullable ItemStack getItem() {
                    return ItemCreator.of(Material.OAK_DOOR)
                            .name(guiRegistry.getString(menuNamespace + "." + path + ".name"))
                            .lore(guiRegistry.getStringList(menuNamespace + "." + path + ".lore"))
                            .get();
                }

                @Override
                public void onClick(@NotNull Player p, @NotNull ClickType clickType) {
                    Bukkit.getScheduler().runTask(FarmQuest.getInstance(), runnable);
                }
            });


            displayTo(player);
        }
    }
}
