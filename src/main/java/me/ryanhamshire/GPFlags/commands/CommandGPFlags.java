package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandGPFlags implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("gpflags.command.reload")) {
                MessagingUtil.sendMessage(commandSender, TextMode.Err, Messages.NoCommandPermission, command.toString());
                return true;
            }
            me.ryanhamshire.GPFlags.GPFlags.getInstance().reloadConfig();
            GPFlags.getInstance().getFlagsDataStore().loadMessages();
            MessagingUtil.sendMessage(commandSender, TextMode.Success, Messages.ReloadComplete);
            return true;
        }
        if (!commandSender.hasPermission("gpflags.command.help")) {
            MessagingUtil.sendMessage(commandSender, TextMode.Err, Messages.NoCommandPermission, command.toString());
            return true;
        }
        List<Command> cmdList = PluginCommandYamlParser.parse(GPFlags.getInstance());
        for (Command c : cmdList) {
            if (c.getPermission() == null || commandSender.hasPermission(c.getPermission())) {
                MessagingUtil.sendMessage(commandSender, TextMode.Info + c.getUsage());
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 1) {
            if (commandSender.hasPermission("gpflags.command.reload")) {
                list.add("reload");
            }
            if (commandSender.hasPermission("gpflags.command.help")) {
                list.add("help");
            }
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        }
        return null;
    }
}
