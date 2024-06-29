package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_ExitPlayerCommand extends PlayerMovementFlagDefinition {

    public FlagDef_ExitPlayerCommand(FlagManager manager, GPFlags plugin) {
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

        executeFlagCommands(flagFrom, player, claimFrom);
    }

    public void executeFlagCommands(Flag flag, Player player, Claim claim) {
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
        return "ExitPlayerCommand";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerCommandRequired));
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
