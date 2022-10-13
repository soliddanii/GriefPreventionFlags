package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BoundingBox;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GriefPrevention.events.ClaimPermissionCheckEvent;


/**
 *
 * @author soliddanii
 */
public class FlagDef_AllowItemFrameRotation extends FlagDefinition
{

    public FlagDef_AllowItemFrameRotation(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClaimPersmissionCheck(ClaimPermissionCheckEvent event) {
        Player player = event.getCheckedPlayer();

        if(event.getDenialReason() == null) return;

        Flag flag = this.getFlagInstanceAtLocation(event.getClaim().getLesserBoundaryCorner(), player);
        if (flag == null) return;

        Event triggeringEvent = event.getTriggeringEvent();
        if(triggeringEvent != null && triggeringEvent instanceof BlockPlaceEvent) {
            BlockPlaceEvent bpe = (BlockPlaceEvent) triggeringEvent;

            if(bpe.getBlockPlaced() != null && bpe.getBlockAgainst() != null && 
                bpe.getBlockPlaced().getType() == Material.AIR && bpe.getBlockAgainst().getType() == Material.AIR) {

                Collection<Entity> nearbyEntities = bpe.getBlockAgainst().getLocation().getWorld().getNearbyEntities(BoundingBox.of(bpe.getBlockAgainst()));

                if(nearbyEntities.stream().anyMatch(entity -> {
                    if(entity != null && entity instanceof ItemFrame) {
                        ItemFrame frame = (ItemFrame) entity;
                        if(frame.getItem() != null && frame.getItem().getType() != Material.AIR) {
                            return true;
                        }
                    }

                    return false;
                })) {
                    event.setDenialReason(null);
                }                    
            }
        }
    }

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();

        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;

        Entity e = event.getRightClicked();
        
        if(e != null && e instanceof ItemFrame && event.isCancelled()){
            ItemFrame frame = (ItemFrame) e;
            if(frame.getItem() != null) {
                frame.setRotation(frame.getRotation().rotateClockwise());
                player.playSound(frame.getLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS, 1000.0f, 1.0f);
                player.sendMessage("Artificial rotation");
            }
        }
    }*/

    @Override
    public String getName() 
    {
        return "AllowItemFrameRotation";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableAllowItemFrameRotation);
    }

    @Override
    public MessageSpecifier getUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableAllowItemFrameRotation);
    }
    
    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }
    
}
