package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
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
 * @author soliddanii
 */
public class FlagDef_PrivateChatDiscord extends FlagDefinition
{

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        // Get the flag from the player location
        Player player = event.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        // Indicate that the message is private
        String colorCode = event.getMessage().startsWith("*") ? "f" : "7";
        String newDisplayName = player.getDisplayName().replaceAll("\\s?((?:§\\w)?\\w+(?:§\\w)?)$", " §"+colorCode+"[P] $1");
        event.setFormat(newDisplayName.trim()+": %2$s");
        
        // Bypass the flag
        if(event.getMessage().startsWith("*"))
        {
            event.setMessage("§r"+event.getMessage().substring(1));
            return;
        }
        
        // Delete all players from the message recipents
        event.getRecipients().clear();
        
        // Get the claim data
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, playerData.lastClaim);
        
        if(claim != null)
        {
            for (Player recipient : Bukkit.getServer().getOnlinePlayers()) 
            {
                // If the receiver is in the same claim or has permission to listen in other private claims add him to the recipients:
                Claim temp = GriefPrevention.instance.dataStore.getClaimAt(recipient.getLocation(), true, GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).lastClaim);
                if ((temp != null && temp.getID().equals(claim.getID())) || recipient.hasPermission("gpflags.bypass")) 
                {
                    event.getRecipients().add(recipient);
                } 
            }
        }
        
    }
    
    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(GameChatMessagePreProcessEvent event) {
        // Get the flag from the player location
        Player player = event.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        
        // Bypass the flag
        if(event.getMessage().startsWith("§r"))
        {
            event.setMessage(event.getMessage().substring(2));
            return;
        }
        
        event.setCancelled(true);
    }
    
    public FlagDef_PrivateChatDiscord(FlagManager manager, GPFlags plugin) {
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
