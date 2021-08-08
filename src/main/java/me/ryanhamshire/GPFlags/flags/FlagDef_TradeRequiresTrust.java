package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

/**
 *
 * @author Daniel Catalán (soliddanii)
 */
public class FlagDef_TradeRequiresTrust extends FlagDefinition {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}

		if (event.getInventory().getType() != InventoryType.MERCHANT) {
			return;
		}

		Player player = (Player) event.getPlayer();

		Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
		if (flag == null) {
			return;
		}
		
		if(player.hasPermission("gpflags.bypass")) {
			return;
		}
		
		PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
		if (playerData == null) {
			return;
		}

		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, playerData.lastClaim);
		if (claim == null) {
			return;
		}
		
		if (claim.allowAccess(player) != null) {
			Util.sendClaimMessage(player, TextMode.Err, "You need access trust to trade in this claim.");
			event.setCancelled(true);
		}
	}

	public FlagDef_TradeRequiresTrust(FlagManager manager, GPFlags plugin) {
		super(manager, plugin);
	}

	@Override
	public String getName() {
		return "TradeRequiresTrust";
	}

	@Override
	public MessageSpecifier getSetMessage(String parameters) {
		return new MessageSpecifier(Messages.EnableTradeRequiresTrust);
	}

	@Override
	public MessageSpecifier getUnSetMessage() {
		return new MessageSpecifier(Messages.DisableTradeRequiresTrust);
	}

	@Override
	public List<FlagType> getFlagType() {
		return Arrays.asList(FlagType.CLAIM);
	}

}
