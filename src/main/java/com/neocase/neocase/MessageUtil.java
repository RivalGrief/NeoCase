package com.neocase.neocase;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtil {
    public static void sendSuccess(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + message);
    }

    public static void sendError(Player player, String message) {
        player.sendMessage(ChatColor.RED + message);
    }
}