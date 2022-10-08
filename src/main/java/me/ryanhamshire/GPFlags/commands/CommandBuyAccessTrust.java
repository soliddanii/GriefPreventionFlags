package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CommandBuyAccessTrust implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            Util.sendMessage(sender, TextMode.Err, Messages.PlayerOnlyCommand, command.toString());
            return true;
        }
        Player player = (Player) sender;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim == null) {
            Util.sendMessage(sender, TextMode.Err, Messages.CannotBuyTrustHere);
            return true;
        }

        Collection<Flag> flags = GPFlags.getInstance().getFlagManager().getFlags(claim.getID().toString());
        for (Flag flag : flags) {
            if (flag.getFlagDefinition().getName().equalsIgnoreCase("BuyAccessTrust")) {
                if (claim.getPermission(player.getUniqueId().toString()) == ClaimPermission.Access ||
                        claim.getPermission(player.getUniqueId().toString()) == ClaimPermission.Build ||
                        claim.getPermission(player.getUniqueId().toString()) == ClaimPermission.Inventory ||
                        player.getUniqueId().equals(claim.getOwnerID())
                ) {
                    Util.sendMessage(sender, TextMode.Err, Messages.AlreadyHaveTrust);
                    return true;
                }
                if (flag.parameters == null || flag.parameters.isEmpty()) {
                    Util.sendMessage(sender, TextMode.Err, Messages.ProblemWithFlagSetup);
                    return true;
                }
                double cost;
                try {
                    cost = Double.parseDouble(flag.parameters);
                } catch (NumberFormatException e) {
                    Util.sendMessage(sender, TextMode.Err, Messages.ProblemWithFlagSetup);
                    return true;
                }
                if (!VaultHook.takeMoney(player, cost)) {
                    Util.sendMessage(sender, TextMode.Err, Messages.NotEnoughMoney);
                    return true;
                }
                if (claim.getOwnerID() != null) {
                    VaultHook.giveMoney(claim.getOwnerID(), cost);
                }
                claim.setPermission(player.getUniqueId().toString(), ClaimPermission.Access);
                Util.sendMessage(sender, TextMode.Info, Messages.BoughtTrust, flag.parameters);
                return true;
            }
        }
        Util.sendMessage(sender, TextMode.Err, Messages.CannotBuyTrustHere);
        return true;
    }
}
