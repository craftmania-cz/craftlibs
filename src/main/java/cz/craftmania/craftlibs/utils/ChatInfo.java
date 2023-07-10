package cz.craftmania.craftlibs.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
    @Deprecated
    DANGER("⻬", new Color(239, 87, 86)),
    ERROR("⻬", new Color(239, 87, 86)),

    /**
     * Zpráva o úspěšné provedené akci.
     */
    SUCCESS("⻪", new Color(84, 160, 63)),

    /**
     * Zpráva používající se hlavně k zasílání informačních ekonomických zpráv.
     */
    @Deprecated
    ECONOMY("⻮", new Color(35, 120, 90)),
    BANK_SUCCESS("⻮", new Color(35, 120, 90)),
    BANK_DANGER("쇛", new Color(239, 87, 86)),
    BANK_INFO("쇜", new Color(243, 196, 76)),

    /**
     * Zpráva s vyšší prioritou a prefixem [Server].
     */
    SERVER("⼧", new Color(88, 130, 180));

    private String key;
    private final Color color;
    private String message;
    private String overrideKey;

    ChatInfo(String key, Color color) {
        this.key = key;
        this.color = color;
        this.message = "";
        this.overrideKey = null;
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
        player.sendMessage(processMessage(message, null));
        if (overrideKey != null) this.overrideKey = null;
    }

    /**
     * Odešle zvolenou zprávu online hráči.
     *
     * Barvu dle klíče {@link ChatInfo} lze v textu používat jako "{c}"
     *
     * @param player Online hráč
     * @param message Zpráva, co se pošle hráči
     * @param component {@link ComponentLike} text pro hover efekt nad textem
     */
    public void send(@NotNull Player player, @NotNull String message, @Nullable ComponentLike component) {
        this.message = message;
        player.sendMessage(processMessage(message, component));
        if (overrideKey != null) this.overrideKey = null;
    }

    /**
     * Odešle zvolenou zprávu {@link CommandSender} objektu, což může být {@link Player} i konzole.<br>
     * Barvu dle klíče {@link ChatInfo} lze v textu používat jako "{c}"
     * @param sender {@link CommandSender} - Online hráč či konzole
     * @param message Zpráva, co se pošle hráči či konzoli
     */
    public void send(@NotNull CommandSender sender, @NotNull String message) {
        this.message = message;
        sender.sendMessage(processMessage(message, null));
        if (overrideKey != null) this.overrideKey = null;
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
    public void send(@NotNull String player, @NotNull String message) {
        this.message = message;
        if (Bukkit.getPlayer(player) == null) {
            return;
        }
        Player onlinePlayer = Bukkit.getPlayer(player);
        assert onlinePlayer != null;
        onlinePlayer.sendMessage(processMessage(message, null));
        if (overrideKey != null) this.overrideKey = null;
    }

    /**
     * Odešle vytvořenou zprávu hráči, pokud je online.
     * Pokud hráč není online, metoda neprovede nic a vrátí void.
     *
     * Barvu dle klíče {@link ChatInfo} lze v textu používat jako "{c}"
     *
     * @param player Online hráč
     * @param message Zpráva, co se pošle hráči
     * @param component {@link ComponentLike} text pro hover efekt nad textem
     */
    public void send(@NotNull String player, @NotNull String message, @Nullable ComponentLike component) {
        this.message = message;
        if (Bukkit.getPlayer(player) == null) {
            return;
        }
        Player onlinePlayer = Bukkit.getPlayer(player);
        assert onlinePlayer != null;
        onlinePlayer.sendMessage(processMessage(message, component));
        if (overrideKey != null) this.overrideKey = null;
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

    /**
     * Obohatí zprávu barvou, která byla zvolena klíčem v {@link ChatInfo}.<br>
     * Barvu dle klíče {@link ChatInfo} lze v textu používat jako "{c}"
     * @param message Zpráva na zpracování
     * @return Zpracovaná zpráva s barvou
     */
    public @NotNull ComponentLike processMessage(@NotNull String message, @Nullable ComponentLike hoverText) {
        TextColor textColor = TextColor.color(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
        if (overrideKey != null) {
            return Component.text(this.overrideKey).color(NamedTextColor.WHITE).append(Component.text(" ")).
                    append(Component.text(message).color(textColor));
        }
        TextReplacementConfig colorReset = TextReplacementConfig.builder().match("{c}").replacement(Component.text().color(textColor)).build();
        if (hoverText != null) {
            HoverEvent<Component> prefixHover = HoverEvent.showText(this.prepareHoverText(this.key));
            HoverEvent<Component> textHover = HoverEvent.showText(hoverText);
            return Component.empty().append(Component.text(this.key).hoverEvent(prefixHover).color(NamedTextColor.WHITE))
                    .append(Component.text(" ")).append(Component.text(message).replaceText(colorReset).color(textColor).hoverEvent(textHover));
        }
        HoverEvent<Component> prefixHover = HoverEvent.showText(this.prepareHoverText(this.key));
        return Component.empty().append(Component.text(this.key).hoverEvent(prefixHover).color(NamedTextColor.WHITE))
                .append(Component.text(" ")).append(Component.text(message).replaceText(colorReset).color(textColor));
    }

    /**
     * Přepis nastaveného prefixu na jiný, na základě Stringu.
     *
     * @param prefixKey Znak, který bude použit jako prefix.
     * @return {@link ChatInfo}
     * @param <T> Prefix
     */
    @Deprecated
    public <T extends String> ChatInfo overridePrefix(T prefixKey) {
        this.overrideKey = prefixKey;
        return this;
    }

    private ComponentLike prepareHoverText(String key) {
        switch (key) {
            case "⻫" -> { // Info
                return Component.text("Informační zpráva").color(ServerColors.INFO.getTextColor());
            }
            case "⻬" -> { // Error
                return Component.text("Chyba").color(ServerColors.DANGER.getTextColor());
            }
            case "⻪" -> { // Success
                return Component.text("Úspěch").color(ServerColors.SUCCESS.getTextColor());
            }
            case "⻮", "쇛", "쇜" -> { // Bank
                return Component.text("Server banka").color(ServerColors.ECONOMY.getTextColor());
            }
            case "⼧" -> { // Server
                return Component.text("Server").color(ServerColors.SERVER.getTextColor());
            }
            default -> {
                return Component.empty();
            }
        }
    }
}
