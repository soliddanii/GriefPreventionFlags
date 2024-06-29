package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.GPFlagsConfig;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.hooks.PlaceholderApiHook;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class MessagingUtil {

     /**
      * Fills in message params, adds formatting, and sends it to the receiver.
     * @param receiver person to get message or null if console
     * @param messageID
     * @param args
     */
    public static void sendMessage(@Nullable CommandSender receiver, Messages messageID, String... args) {
        String message = GPFlags.getInstance().getFlagsDataStore().getMessage(messageID, args);
        sendMessage(receiver, message);
    }

    /**
     * Send a {@link Messages Message} to a player, or console if player is null
     *
     * @param receiver  Player to send message to, or null if to console
     * @param color     Color of message
     * @param messageID Message to send
     * @param args      Message parameters
     */
    public static void sendMessage(@Nullable CommandSender receiver, String color, Messages messageID, String... args) {
        String message = GPFlags.getInstance().getFlagsDataStore().getMessage(messageID, args);
        sendMessage(receiver, color + message);
    }

    public static void sendMessage(@Nullable CommandSender receiver, String message) {
        if (!(receiver instanceof Player)) {
            logToConsole(message);
            return;
        }
        Player player = (Player) receiver;
        try {
            message = PlaceholderApiHook.addPlaceholders(player, message);
        } catch (Throwable ignored) {}
        Component component = MiniMessage.miniMessage().deserialize(message);
        Audience.audience(player).sendMessage(component);
    }

    private static void logToConsole(String message) {
        String stripped = MiniMessage.miniMessage().stripTags(message);
        GPFlags.getInstance().getLogger().info(stripped);
    }

    public static void sendActionbar(Player player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        Audience.audience(player).sendActionBar(component);
    }

    public static void logFlagCommands(String log) {
        if (GPFlagsConfig.LOG_ENTER_EXIT_COMMANDS) logToConsole(log);
    }

    /**
     * We used to use <#rrggbb> but minimessage converter wont recognize that as a chatcolor
     * This converts <#rrggbb> to &#rrggbb
     * @param string
     * @return
     */
    private static String convertOriginalHexColors(String string) {
        string = string.replace(COLOR_CHAR, '&');
        Pattern hexPattern2 = Pattern.compile("<#([A-Fa-f0-9]){6}>");
        Matcher matcher = hexPattern2.matcher(string);
        while (matcher.find()) {
            String hexColor = "&#" + matcher.group().substring(1, matcher.group().length() - 1);
            final String before = string.substring(0, matcher.start());
            final String after = string.substring(matcher.end());
            string = before + hexColor + after;
            matcher = hexPattern2.matcher(string);
        }
        return string;
    }

    public static String reserialize(String ampersandMessage) {
        ampersandMessage = convertOriginalHexColors(ampersandMessage);
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(ampersandMessage);
        return MiniMessage.miniMessage().serialize(component);
    }
}
