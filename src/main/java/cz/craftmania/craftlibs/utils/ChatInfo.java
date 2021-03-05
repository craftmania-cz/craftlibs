package cz.craftmania.craftlibs.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

public enum ChatInfo {

    INFO("⻫", new Color(244, 158, 11)),
    DANGER("⻬", new Color(220, 38, 37)),
    SUCCESS("⻪", new Color(133, 204, 22)),
    ECONOMY("⻮", new Color(17, 96, 67));

    private final String key;
    private final Color color;

    ChatInfo(String key, Color color) {
        this.key = key;
        this.color = color;
    }

    public void send(Player player, String message) {
        player.sendMessage(this.key + " " + ChatColor.of(this.color) + message);
    }

    public void send(String player, String message) {
        if (Bukkit.getPlayer(player) == null) {
            return;
        }
        Player onlinePlayer = Bukkit.getPlayer(player);
        assert onlinePlayer != null;
        onlinePlayer.sendMessage(this.key + " " + ChatColor.of(this.color) + message);
    }
}
