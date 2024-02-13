package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_AllowInfest extends FlagDefinition {

    public FlagDef_AllowInfest(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "AllowInfest";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledAllowInfest);
    }

    @EventHandler
    public void onInfest(EntityChangeBlockEvent event) {
        Flag flag = this.getFlagInstanceAtLocation(event.getBlock().getLocation(), null);
        if (flag == null) return;
        if (event.getEntity().getType() == EntityType.SILVERFISH) {
            event.setCancelled(false);
        }
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledAllowInfest);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM);
    }

}
