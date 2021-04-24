package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;


/**
 *
 * @author Daniel Catalán (soliddanii)
 */
public class FlagDef_PrivateChat extends FlagDefinition
{

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        // Get the flag from the player location
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        // Indicate that the message is private
        String colorCode = event.getMessage().startsWith("*") ? "f" : "7";
        String newDisplayName = player.getDisplayName().replaceAll("\\s?((?:§\\w)?\\w+(?:§\\w)?)$", " §"+colorCode+"[P] $1");
        event.setFormat(newDisplayName.trim()+": %2$s");
        
        // Bypass the flag
        if(event.getMessage().startsWith("*"))
        {
            event.setMessage(event.getMessage().substring(1));
            return;
        }
        
        // Borrar todos los jugadores receptores del mensaje
        event.getRecipients().clear();
        
        // Get the claim data
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, playerData.lastClaim);
        
        if(claim != null)
        {
            for (Player recipient : Bukkit.getServer().getOnlinePlayers()) 
            {
                // Si el receptor esta en la misma claim o tiene permisos para escuchar en claims privadas lo añadimos a la lista de receptores:
                Claim temp = GriefPrevention.instance.dataStore.getClaimAt(recipient.getLocation(), true, GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).lastClaim);
                if ((temp != null && temp.getID().equals(claim.getID())) || recipient.hasPermission("gpflags.bypass")) 
                {
                    event.getRecipients().add(recipient);
                } 
            }
        }
        
    }
    
    public FlagDef_PrivateChat(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() 
    {
        return "PrivateChat";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnablePrivateChat);
    }

    @Override
    public MessageSpecifier getUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisablePrivateChat);
    }
    
    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }
    
}
