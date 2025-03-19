package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.farmquest.FarmQuest;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        FarmQuest plugin = FarmQuest.getInstance();

        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("pickquest")) {
                new PickQuestCommand().onSubCommand(sender, command, s, args);
                return true;
            }
            else if(args[0].equalsIgnoreCase("farmmarker")) {
                new FarmMarkerCommand().onSubCommand(sender, command, s, args);
                return true;
            }
            else if(args[0].equalsIgnoreCase("createregion")) {
                new CreateRegionCommand().onSubCommand(sender, command, s, args);
                return true;
            }
            else if(args[0].equalsIgnoreCase("reload")) {
                new ReloadCommand().onSubCommand(sender, command, s, args);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> completions = new ArrayList<>(List.of("pickquest"));
            for(String arg : Arrays.asList("farmmarker", "createregion", "reload")) {
                if(sender.hasPermission("farmquest." + arg)) completions.add(arg);
            }
            return completions;
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("pickquest")) {
            return Quest.getRegisteredQuests().stream().map(Quest::getId).collect(Collectors.toList());
        }
        return List.of();
    }
}
