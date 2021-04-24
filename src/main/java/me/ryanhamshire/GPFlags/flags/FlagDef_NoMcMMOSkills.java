package me.ryanhamshire.GPFlags.flags;

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMcMMOSkills extends FlagDefinition {

    public FlagDef_NoMcMMOSkills(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDisarm(McMMOPlayerDisarmEvent event) {
        this.handleEvent(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerAbility(McMMOPlayerAbilityActivateEvent event) {
        this.handleEvent(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerSecondaryAbility(SubSkillEvent event) {
        this.handleEvent(event.getPlayer(), event);
    }

    private void handleEvent(Player player, Cancellable event) {
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag != null) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "NoMcMMOSkills";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoMcMMOSkills);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoMcMMOSkills);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
