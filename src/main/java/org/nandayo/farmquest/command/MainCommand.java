package org.nandayo.farmquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nandayo.farmquest.model.farm.Farm;
import org.nandayo.farmquest.model.quest.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("farmer")) {
                return new FarmerMenuCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("deliver")) {
                return new DeliverCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("farmmarker")) {
                return new FarmMarkerCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("createregion")) {
                return new CreateRegionCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("resizeregion")) {
                return new ResizeRegionCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("deleteregion")) {
                return new DeleteRegionCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("reload")) {
                return new ReloadCommand().onSubCommand(sender, s, args);
            } else if (args[0].equalsIgnoreCase("farmmanager")) {
                return new FarmManagerCommand().onSubCommand(sender, s, args);
            } else if(args[0].equalsIgnoreCase("items")) {
               return new ItemsCommand().onSubCommand(sender, s, args);
            } else if(args[0].equalsIgnoreCase("removecompleted")) {
                return new RemoveCompletedCommand().onSubCommand(sender, s, args);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> completions = new ArrayList<>(List.of("deliver","farmer"));
            for(String arg : Arrays.asList("farmmarker","createregion","resizeregion","deleteregion","reload","farmmanager","items","removecompleted")) {
                if(sender.hasPermission("farmquest." + arg)) completions.add(arg);
            }
            return completions;
        }
        else if(args.length == 2 && Arrays.asList("farmer","deliver","farmmanager","resizeregion","deleteregion").contains(args[0])) {
            return Farm.getRegisteredFarms().stream().filter(Objects::nonNull).map(Farm::getId).toList();
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("removecompleted")) {
            return Quest.getRegisteredQuests().stream().filter(Objects::nonNull).map(Quest::getId).toList();
        }
        else if(args.length == 3 && args[0].equalsIgnoreCase("removecompleted")) {
            return null;
        }
        return List.of();
    }
}
