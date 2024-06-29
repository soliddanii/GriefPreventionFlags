package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class Util {

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major    Major version to check (Most likely just going to be 1)
     * @param minor    Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = getMinecraftVersion().split("\\.");
        int maj = Integer.parseInt(version[0]);
        int min = Integer.parseInt(version[1]);
        int rev;
        try {
            rev = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj > major || min > minor || (min == minor && rev >= revision);
    }

    /**
     * Get the Minecraft version the server is running
     *
     * @return Minecraft version the server is running
     */
    public static String getMinecraftVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    /**
     * Get the ItemStack form of a vehicle
     * <p>Specifically a boat or minecart</p>
     *
     * @param vehicle Vehicle to get item from
     * @return ItemStack that matches vehicle
     */
    public static ItemStack getItemFromVehicle(Vehicle vehicle) {
        Material material = null;
        if (vehicle instanceof Boat) {
            switch (((Boat) vehicle).getWoodType()) {
                case BIRCH:
                    material = Material.BIRCH_BOAT;
                    break;
                case ACACIA:
                    material = Material.ACACIA_BOAT;
                    break;
                case JUNGLE:
                    material = Material.JUNGLE_BOAT;
                    break;
                case REDWOOD:
                    material = Material.SPRUCE_BOAT;
                    break;
                case DARK_OAK:
                    material = Material.DARK_OAK_BOAT;
                    break;
                default:
                    material = Material.OAK_BOAT;
            }
        } else if (vehicle instanceof RideableMinecart) {
            material = Material.MINECART;
        } else if (vehicle instanceof StorageMinecart) {
            material = Material.CHEST_MINECART;
        } else if (vehicle instanceof CommandMinecart) {
            material = Material.COMMAND_BLOCK_MINECART;
        } else if (vehicle instanceof ExplosiveMinecart) {
            material = Material.TNT_MINECART;
        } else if (vehicle instanceof HopperMinecart) {
            material = Material.HOPPER_MINECART;
        } else if (vehicle instanceof PoweredMinecart) {
            material = Material.FURNACE_MINECART;
        }
        if (material != null) {
            return new ItemStack(material);
        }
        return null;
    }

    /**
     * Check if an ItemStack is a vehicle
     * <p>Specifically a boat or minecart</p>
     *
     * @param itemStack ItemStack to check
     * @return True if item is a vehicle
     */
    public static boolean isAVehicle(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case MINECART:
            case CHEST_MINECART:
            case COMMAND_BLOCK_MINECART:
            case FURNACE_MINECART:
            case HOPPER_MINECART:
            case TNT_MINECART:
            case BIRCH_BOAT:
            case ACACIA_BOAT:
            case DARK_OAK_BOAT:
            case JUNGLE_BOAT:
            case OAK_BOAT:
            case SPRUCE_BOAT:
                return true;
        }
        return false;
    }

    public static boolean isMonster(Entity entity) {
        EntityType type = entity.getType();
        return (entity instanceof Monster || type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SHULKER
                || type == EntityType.PHANTOM || type == EntityType.SLIME || type == EntityType.HOGLIN);
    }

    public static boolean canAccess(Claim claim, Player player) {
        if (claim == null) return true;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (playerData.ignoreClaims) return true;
        try {
            return claim.checkPermission(player, ClaimPermission.Access, null) == null;
        } catch (NoSuchMethodError e) {
            return claim.allowAccess(player) == null;
        }
    }


    public static boolean canInventory(Claim claim, Player player) {
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (playerData.ignoreClaims) return true;
        try {
            return claim.checkPermission(player, ClaimPermission.Inventory, null) == null;
        } catch (NoSuchFieldError | NoSuchMethodError e) {
            return claim.allowContainers(player) == null;
        }
    }

    public static boolean canBuild(Claim claim, Player player) {
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (playerData.ignoreClaims) return true;
        try {
            return claim.checkPermission(player, ClaimPermission.Build, null) == null;
        } catch (NoSuchFieldError | NoSuchMethodError e) {
            return claim.allowBuild(player, Material.STONE) == null;
        }
    }

    public static boolean canManage(Claim claim, Player player) {
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (playerData.ignoreClaims) return true;
        try {
            return claim.checkPermission(player, ClaimPermission.Manage, null) == null;
        } catch (NoSuchFieldError | NoSuchMethodError e) {
            return claim.allowGrantPermission(player) == null;
        }
    }

    public static boolean canEdit(Player player, Claim claim) {
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (playerData.ignoreClaims) return true;
        try {
            return claim.checkPermission(player, ClaimPermission.Edit, null) == null;
        } catch (NoSuchFieldError e) {
            return claim.allowEdit(player) == null;
        }
    }

    public static List<String> flagTab(CommandSender sender, String arg) {
        List<String> flags = new ArrayList<>();
        GPFlags.getInstance().getFlagManager().getFlagDefinitions().forEach(flagDefinition -> {
            if (sender.hasPermission("gpflags.flag." + flagDefinition.getName())) {
                flags.add(flagDefinition.getName());
            }
        });
        return StringUtil.copyPartialMatches(arg, flags, new ArrayList<>());
    }

    public static List<String> paramTab(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "commandblacklist":
            case "commandwhitelist":
            case "entercommand":
            case "entercommand_members":
            case "entercommand_owner":
            case "exitcommand":
            case "exitcommand_members":
            case "exitcommand_owner":
            case "entermessage":
            case "exitmessage":
                List<String> params = new ArrayList<>();
                if (!(sender instanceof Player)) return null;
                Player p = (Player) sender;
                FlagDefinition flagD = (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("entercommand"));
                Flag flag = flagD.getFlagInstanceAtLocation(p.getLocation(), p);
                if (flag == null) return null;
                String flagParams = flag.parameters;
                if (flagParams != null) {
                    params.add(flagParams);
                }
                return StringUtil.copyPartialMatches(args[1], params, new ArrayList<>());
            case "noenterplayer":
                if (!(sender instanceof Player)) return null;
                Player p2 = (Player) sender;
                FlagDefinition flagD2 = (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("noenterplayer"));
                Flag flag2 = flagD2.getFlagInstanceAtLocation(p2.getLocation(), p2);
                if (flag2 == null) return null;
                String flagParams2 = flag2.parameters;
                if (flagParams2 == null) return null;
                ArrayList<String> suggestion = new ArrayList<>();
                suggestion.add(flag2.getFriendlyParameters());
                return StringUtil.copyPartialMatches(args[1], suggestion, new ArrayList<>());
            case "nomobspawnstype":
                List<String> entityTypes = new ArrayList<>();
                for (EntityType entityType : EntityType.values()) {
                    String type = entityType.toString();
                    if (sender.hasPermission("gpflags.flag.nomobspawnstype." + type)) {
                        String arg = args[1];
                        if (arg.contains(";")) {
                            if (arg.charAt(arg.length() - 1) != ';') {
                                arg = arg.substring(0, arg.lastIndexOf(';') + 1);
                            }
                            entityTypes.add(arg + type);
                        } else {
                            entityTypes.add(type);
                        }
                    }
                }
                return StringUtil.copyPartialMatches(args[1], entityTypes, new ArrayList<>());

            case "changebiome":
                ArrayList<String> biomes = new ArrayList<>();
                for (Biome biome : Biome.values()) {
                    if (sender.hasPermission("gpflags.flag.changebiome." + biome)) {
                        biomes.add(biome.toString());
                    }
                }
                biomes.sort(String.CASE_INSENSITIVE_ORDER);
                return StringUtil.copyPartialMatches(args[1], biomes, new ArrayList<>());

            case "noopendoors":
                if (args.length != 2) return null;
                List<String> doorType = Arrays.asList("doors", "trapdoors", "gates");
                return StringUtil.copyPartialMatches(args[1], doorType, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    public static int getMaxHeight(Location l) {
        return getMaxHeight(l.getWorld());
    }

    public static int getMinHeight(Location l) {
        return getMinHeight(l.getWorld());
    }

    public static int getMaxHeight(World w) {
        try {
            return w.getMaxHeight();
        } catch (NoSuchMethodError e) {
            return 256;
        }
    }

    public static int getMinHeight(World w) {
        try {
            return w.getMinHeight();
        } catch (NoSuchMethodError e) {
            return 0;
        }
    }

    public static Location getInBoundsLocation(Player p) {
        Location loc = p.getLocation();
        World world = loc.getWorld();
        int maxHeight = Util.getMaxHeight(world);
        if (loc.getBlockY() >= maxHeight) {
            loc.setY(maxHeight - 1);
        }
        return loc;
    }

    public static boolean isClaimOwner(Claim c, Player p) {
        if (c == null) return false;
        if (c.getOwnerID() == null) return false;
        return c.getOwnerID().equals(p.getUniqueId());
    }

    public static boolean shouldBypass(@NotNull Player p, @Nullable Claim c, @NotNull String basePerm) {
        if (p.hasPermission(basePerm)) return true;
        if (c == null) return p.hasPermission(basePerm + ".nonclaim");
        if (c.getOwnerID() == null && p.hasPermission(basePerm + ".adminclaim")) return true;
        if (isClaimOwner(c, p) && p.hasPermission(basePerm + ".ownclaim")) return true;
        if (canManage(c, p) && p.hasPermission(basePerm + ".manage")) return true;
        if (canBuild(c, p) && (p.hasPermission(basePerm + ".build") || p.hasPermission(basePerm + ".edit"))) return true;
        if (canInventory(c, p) && p.hasPermission(basePerm + ".inventory")) return true;
        if (canAccess(c, p) && p.hasPermission(basePerm + ".access")) return true;
        return false;
    }

    public static boolean shouldBypass(Player p, Claim c, Flag f) {
        String basePerm = "gpflags.bypass." + f.getFlagDefinition().getName();
        return shouldBypass(p, c, basePerm);
    }

    public static HashSet<Player> getPlayersIn(Claim claim) {
        HashSet<Player> players = new HashSet<>();
        World world = claim.getGreaterBoundaryCorner().getWorld();
        for (Player p : world.getPlayers()) {
            if (claim.contains(p.getLocation(), false, false)) {
                players.add(p);
            }
        }
        return players;
    }

    /**
     * Gets a list of all flags the user has permission for
     * @param player The player whose perms we want to check
     * @return A message showing all the flags player can use
     */
    public static String getAvailableFlags(Permissible player) {
        StringBuilder flagDefsList = new StringBuilder();
        Collection<FlagDefinition> defs = GPFlags.getInstance().getFlagManager().getFlagDefinitions();
        List<FlagDefinition> sortedDefs = new ArrayList<>(defs);
        sortedDefs.sort(Comparator.comparing(FlagDefinition::getName));

        flagDefsList.append("<aqua>");
        for (FlagDefinition def : sortedDefs) {
            if (player.hasPermission("gpflags.flag." + def.getName())) {
                flagDefsList.append(def.getName()).append("<grey>,<aqua> ");
            }
        }
        String def = flagDefsList.toString();
        if (def.length() > 5) {
            def = def.substring(0, def.length() - 4);
        }
        return def;
    }

}
