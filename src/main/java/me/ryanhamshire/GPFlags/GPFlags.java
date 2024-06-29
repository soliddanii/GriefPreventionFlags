package me.ryanhamshire.GPFlags;

import java.util.*;

import me.ryanhamshire.GPFlags.commands.*;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.listener.*;
import me.ryanhamshire.GPFlags.metrics.Metrics;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import github.scarsz.discordsrv.DiscordSRV;
import me.ryanhamshire.GPFlags.flags.FlagDef_PrivateChatDiscord;
import me.ryanhamshire.GPFlags.flags.FlagDef_ViewContainers;


/**
 * <b>Main GriefPrevention Flags class</b>
 */
public class GPFlags extends JavaPlugin {

    private static GPFlags instance;
    private FlagsDataStore flagsDataStore;
    private final FlagManager flagManager = new FlagManager();
    private WorldSettingsManager worldSettingsManager;

    boolean registeredFlagDefinitions = false;
    private PlayerListener playerListener;
    
    private FlagDef_PrivateChatDiscord dsrvListener = null;


    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        this.playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        try {
            Class.forName("io.papermc.paper.event.entity.EntityMoveEvent");
            Bukkit.getPluginManager().registerEvents(new EntityMoveListener(), this);
        } catch (ClassNotFoundException ignored) {}

        try {
            Class.forName("me.ryanhamshire.GriefPrevention.events.ClaimResizeEvent");
            Bukkit.getPluginManager().registerEvents(new ClaimResizeListener(), this);
        } catch (ClassNotFoundException e) {
            Bukkit.getPluginManager().registerEvents(new ClaimModifiedListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new ClaimTransferListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlightManager(), this);

        this.flagsDataStore = new FlagsDataStore();
        reloadConfig();

        // Register Commands
        getCommand("allflags").setExecutor(new CommandAllFlags());
        getCommand("gpflags").setExecutor(new CommandGPFlags());
        getCommand("listclaimflags").setExecutor(new CommandListClaimFlags());
        getCommand("setclaimflag").setExecutor(new CommandSetClaimFlag());
        getCommand("setclaimflagplayer").setExecutor(new CommandSetClaimFlagPlayer());
        getCommand("setdefaultclaimflag").setExecutor(new CommandSetDefaultClaimFlag());
        getCommand("setserverflag").setExecutor(new CommandSetServerFlag());
        getCommand("setworldflag").setExecutor(new CommandSetWorldFlag());
        getCommand("unsetclaimflag").setExecutor(new CommandUnsetClaimFlag());
        getCommand("unsetclaimflagplayer").setExecutor(new CommandUnsetClaimFlagPlayer());
        getCommand("unsetdefaultclaimflag").setExecutor(new CommandUnsetDefaultClaimFlag());
        getCommand("unsetserverflag").setExecutor(new CommandUnsetServerFlag());
        getCommand("unsetworldflag").setExecutor(new CommandUnsetWorldFlag());
        getCommand("bulksetflag").setExecutor(new CommandBulkSetFlag());
        getCommand("bulkunsetflag").setExecutor(new CommandBulkUnsetFlag());

        // Subscribe discord srv flag listener
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            DiscordSRV.api.subscribe(this.dsrvListener);
        }
        
        Metrics metrics = new Metrics(this, 17786);
        Set<String> usedFlags = GPFlags.getInstance().getFlagManager().getUsedFlags();
        Collection<FlagDefinition> defs = GPFlags.getInstance().getFlagManager().getFlagDefinitions();
        for (FlagDefinition def : defs) {
            metrics.addCustomChart(new Metrics.SimplePie("using_" + def.getName().toLowerCase(), () -> {
                return String.valueOf(usedFlags.contains(def.getName().toLowerCase()));
            }));
        }

        metrics.addCustomChart(new Metrics.SimplePie("griefprevention_version", () -> {
            return GriefPrevention.instance.getDescription().getVersion();
        }));

        UpdateChecker.run(this, "gpflags");

        float finish = (float) (System.currentTimeMillis() - start) / 1000;
        MessagingUtil.sendMessage(null, "Successfully loaded in " + String.format("%.2f", finish) + " seconds");
    }

    public void onDisable() {
        FlagDef_ViewContainers.getViewingInventories().forEach(inv -> {
            inv.setContents(new ItemStack[inv.getSize()]);
            new ArrayList<>(inv.getViewers()).forEach(HumanEntity::closeInventory);
        });
        if (flagsDataStore != null) {
            flagsDataStore = null;
        }
        instance = null;
        playerListener = null;
        
        // Unsuscribe discord srv flag listener
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV") && this.dsrvListener != null) {
            DiscordSRV.api.unsubscribe(this.dsrvListener);
            this.dsrvListener = null;
        }
    }

    /**
     * Reload the config file
     */
    public void reloadConfig() {
        this.worldSettingsManager = new WorldSettingsManager();
        new GPFlagsConfig(this);
    }

    /**
     * Get an instance of this plugin
     *
     * @return Instance of this plugin
     */
    public static GPFlags getInstance() {
        return instance;
    }

    /**
     * Get an instance of the flags data store
     *
     * @return Instance of the flags data store
     */
    public FlagsDataStore getFlagsDataStore() {
        return this.flagsDataStore;
    }

    /**
     * Get an instance of the flag manager
     *
     * @return Instance of the flag manager
     */
    public FlagManager getFlagManager() {
        return this.flagManager;
    }

    /**
     * Get an instance of the world settings manager
     *
     * @return Instance of the world settings manager
     */
    public WorldSettingsManager getWorldSettingsManager() {
        return this.worldSettingsManager;
    }

    /**
     * Get an instance of the player listener class
     *
     * @return Instance of the player listener class
     */
    public PlayerListener getPlayerListener() {
        return this.playerListener;
    }
    
    /**
     * Get an instance of the discordsrv listener class
     *
     * @return Instance of the discordsrv listener class
     */
    public FlagDef_PrivateChatDiscord getDSRVListener() {
        return this.dsrvListener;
    }
    
    /**
     * Set the instance of the discordsrv listener class
     */
    public void setDSRVListener(FlagDef_PrivateChatDiscord flagDef) {
        this.dsrvListener = flagDef;
    }

}
