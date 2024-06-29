package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_ExitCommand extends PlayerMovementFlagDefinition {

    public FlagDef_ExitCommand(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (claimFrom == null) return;
        Flag flagFrom = plugin.getFlagManager().getEffectiveFlag(lastLocation, this.getName(), claimFrom);
        if (flagFrom == null) return;
        Flag flagTo = plugin.getFlagManager().getEffectiveFlag(to, this.getName(), claimTo);
        if (flagFrom == flagTo) return;
        // moving to different claim with the same params
        if (flagTo != null && flagTo.parameters.equals(flagFrom.parameters)) return;

        if (player.hasPermission("gpflags.bypass.exitcommand")) return;

        executeFlagCommandsFromConsole(flagFrom, player, claimFrom);
    }

    public void executeFlagCommandsFromConsole(Flag flag, Player player, Claim claim) {
        String commandLinesString = flag.parameters.replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
        String ownerName = claim.getOwnerName();
        if (ownerName != null) {
            commandLinesString = commandLinesString.replace("%owner%", ownerName);
        }
        String[] commandLines = commandLinesString.split(";");
        for (String commandLine : commandLines) {
            MessagingUtil.logFlagCommands("Exit command: " + commandLine);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandLine);
        }
    }


    @Override
    public String getName() {
        return "ExitCommand";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.ConsoleCommandRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddedExitCommand, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedExitCommand);
    }

}
