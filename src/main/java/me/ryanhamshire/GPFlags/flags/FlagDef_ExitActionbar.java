package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_ExitActionbar extends PlayerMovementFlagDefinition {

    public FlagDef_ExitActionbar(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (claimFrom == null) return;
        Flag flagFrom = plugin.getFlagManager().getEffectiveFlag(lastLocation, this.getName(), claimFrom);
        if (flagFrom == null) return;
        Flag flagTo = plugin.getFlagManager().getEffectiveFlag(to, this.getName(), claimTo);
        if (flagFrom == flagTo) return;
        // moving to different claim with the same message
        if (flagTo != null && flagTo.parameters.equals(flagFrom.parameters)) return;

        sendActionbar(flagFrom, player, claimFrom);
    }

    public void sendActionbar(Flag flag, Player player, Claim claim) {
        String message = flag.parameters;
        String owner = claim.getOwnerName();
        if (owner != null) {
            message = message.replace("%owner%", owner);
        }
        message = message.replace("%name%", player.getName());
        MessagingUtil.sendActionbar(player, message);
    }

    @Override
    public String getName() {
        return "ExitActionbar";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.ActionbarRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddedExitActionbar, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedExitActionbar);
    }

}
