package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_OwnerFly extends PlayerMovementFlagDefinition implements Listener {

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claim) {
        if (lastLocation == null) return true;
        Flag flag = GPFlags.getInstance().getFlagManager().getFlag(claim, this);
        Flag ownerMember = GPFlags.getInstance().getFlagManager().getFlag(claim, "OwnerMemberFly");

        if (flag == null && ownerMember == null) {
            if (claim != null) {
                Flag noFlight = GPFlags.getInstance().getFlagManager().getFlag(claim, GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("NoFlight"));
                if (noFlight != null && !noFlight.getSet()) {
                    return true;
                }
            }
            if (player.isFlying() && !canFly(player)) {
                Block block = player.getLocation().getBlock();
                while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.setAllowFlight(false);
                if (to.getY() - block.getY() >= 4) {
                    GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                }
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
                return true;
            }
            if (player.getAllowFlight() && !canFly(player)) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return true;
        }
        if (flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;

        if (claim == null) return true;
        if (!claim.getOwnerName().equalsIgnoreCase(player.getName())) {
            if (!canFly(player)) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            if (!canFly(player)) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return true;
        }

        player.setAllowFlight(true);
        GPFlags.sendMessage(player, TextMode.Success, Messages.EnterFlightEnabled);
        return true;
    }

    private boolean canFly(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SPECTATOR || mode == GameMode.CREATIVE ||
                player.hasPermission("gpflags.bypass.fly") || player.hasPermission("gpflags.bypass");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        Material below = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);

        if (flag != null && claim.getOwnerName().equalsIgnoreCase(player.getName())) {
            player.setAllowFlight(true);
            if (below == Material.AIR) {
                player.setFlying(true);
            }
        }
    }

    public FlagDef_OwnerFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "OwnerFly";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.OwnerFlightEnabled);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.OwnerFlightDisabled);
    }

}

