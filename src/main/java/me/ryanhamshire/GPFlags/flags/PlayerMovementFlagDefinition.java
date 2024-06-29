package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.event.PlayerPostClaimBorderEvent;
import me.ryanhamshire.GPFlags.event.PlayerPreClaimBorderEvent;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

/**
 * Base flag definition for movement based flags
 * <p>When creating a flag that requires checks for players moving in/out of claims, extend from this class</p>
 */
@SuppressWarnings("WeakerAccess")
public abstract class PlayerMovementFlagDefinition extends FlagDefinition {

    public PlayerMovementFlagDefinition(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    public boolean allowMovement(Player player, Location from, Location to, Claim claimFrom, Claim claimTo) {
        return true;
    }

    @EventHandler
    public void onPreMove(PlayerPreClaimBorderEvent event) {
        Player player = event.getPlayer();
        Location from = event.getLocFrom();
        Location to = event.getLocTo();
        Claim claimFrom = event.getClaimFrom();
        Claim claimTo = event.getClaimTo();
        if (!this.allowMovement(player, from, to, claimFrom, claimTo)) {
            event.setCancelled(true);
            player.setVelocity(new Vector());
        }
    }

    @EventHandler
    public void onPostMove(PlayerPostClaimBorderEvent event) {
        onChangeClaim(event.getPlayer(), event.getLocFrom(), event.getLocTo(), event.getClaimFrom(), event.getClaimTo());
    }

    /**
     * Called after a player has successfully moved from one region to another.
     * This is not called for player join events but probably will be in a later version.
     * @param player
     * @param from A bound-adjusted location
     * @param to A bound-adjusted location
     * @param claimFrom The claim that the player is coming from
     * @param claimTo The claim that the player is now in
     */
    public void onChangeClaim(Player player, Location from, Location to, Claim claimFrom, Claim claimTo) {}

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
