package cz.craftmania.craftlibs.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Metoda k zasílání jednotných zpráv na serveru dle typu (klíče) a prefixu z resource packu.
 */
public enum ChatInfo {

    /**
     * Základní informační zpráva pro jakýkoliv typ zprávy.
     */
    INFO("⻫", new Color(243, 196, 76)),

    /**
     * Vystražná zpráva, s vyšší prioritou upozornění.
     */
    DANGER("⻬", new Color(239, 87, 86)),

    /**
     * Zpráva o úspěšné provedené akci.
     */
    SUCCESS("⻪", new Color(84, 160, 63)),

    /**
     * Zpráva používající se hlavně k zasílání informačních ekonomických zpráv.
     */
    ECONOMY("⻮", new Color(35, 120, 90)),

    /**
     * Zpráva s vyšší prioritou a prefixem [Server].
     */
    SERVER("⼧", new Color(88, 130, 180));

    private final String key;
    private final Color color;
    private String message;

    ChatInfo(String key, Color color) {
        this.key = key;
        this.color = color;
        this.message = "";
    }

    /**
     * Odešle zvolenou zprávu online hráči.
     *
     * Barvu dle klíče {@link ChatInfo} lze v textu používat jako "{c}"
     *
     * @param player Online hráč
     * @param message Zpráva, co se pošle hráči
     */
    public void send(@NotNull Player player, @NotNull String message) {
        this.message = message;
        player.sendMessage(this.key + " " + ChatColor.of(this.color) + message.replace("{c}",  ChatColor.of(this.color).toString()));
    }

    /**
     * Odešle vytvořenou zprávu hráči, pokud je online.
     * Pokud hráč není online, metoda neprovede nic a vrátí void.
     *
     * Barvu dle klíče {@link ChatInfo} lze v textu používat jako "{c}"
     *
     * @param player Online hráč
     * @param message Zpráva, co se pošle hráči
     */
    @Nullable
    public void send(@NotNull String player, @NotNull String message) {
        this.message = message;
        if (Bukkit.getPlayer(player) == null) {
            return;
        }
        Player onlinePlayer = Bukkit.getPlayer(player);
        assert onlinePlayer != null;
        onlinePlayer.sendMessage(this.key + " " + ChatColor.of(this.color) + message.replace("{c}",  ChatColor.of(this.color).toString()));
    }

    /**
     * Vrací zadanou zprávu při vytváření objektu {@link ChatInfo}<br>
     * 
     * <strong>Zpráva je v raw formátu bez barev podle klíče objektu.</strong>
     *
     * @return String zprávy
     */
    public @NotNull String getMessage() {
        return message;
    }
}
