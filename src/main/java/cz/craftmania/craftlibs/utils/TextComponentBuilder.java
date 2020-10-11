package cz.craftmania.craftlibs.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.core.util.Assert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

/**
 * This library simplifies creating {@link TextComponent}.
 * All strings are automatically parsed with {@link ChatColor#translateAlternateColorCodes(char, String)} and {@link TextComponent#fromLegacyText(String)}.
 * This object is {@link Cloneable}.
 * @version 1.0.0
 * @author jacobbordas / igniss
 */
public class TextComponentBuilder implements Cloneable {

    private final TextComponent component;

    /**
     * Creates TextComponentBuilder with desired message. This message is parsed with {@link ChatColor#translateAlternateColorCodes(char, String)}
     * and with {@link TextComponent#fromLegacyText(String)} so all formats and colours are parsed automatically to {@link TextComponent}.
     * @param message Message to parse to TextComponent
     */
    public TextComponentBuilder(String message) {
        this.component = new BaseComponentMerger(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message))).output(false);
    }

    /**
     * Creates TextComponentBuilder with desired message. This message is parsed with {@link ChatColor#translateAlternateColorCodes(char, String)}
     * and with {@link TextComponent#fromLegacyText(String)} so all formats and colours are parsed automatically to {@link TextComponent}.
     * @param message Message to parse to TextComponent
     * @param centered Whether this message should be centered in {@link TextComponent}
     */
    public TextComponentBuilder(String message, boolean centered) {
        this.component = new BaseComponentMerger(TextComponent.fromLegacyText(centered ? StringUtils.getCenteredMessage(ChatColor.translateAlternateColorCodes('&', message)) : ChatColor.translateAlternateColorCodes('&', message))).output(false);
    }

    /**
     * Sets performed command to the TextComponent.
     * @param command Command you want to se to be performed on click.
     */
    public TextComponentBuilder setPerformedCommand(String command) {
        if (!command.startsWith("/")) command = "/" + command;

        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    /**
     * Sets tooltip to the TextComponent.
     * @param tooltip Tooltip you want to set to the TextComponent on hover.
     */
    public TextComponentBuilder setTooltip(String tooltip) {
        this.component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(tooltip)));
        return this;
    }

    /**
     * Sets suggested command to the TextComponent.
     * @param command Command you want to be suggested on click.
     */
    public TextComponentBuilder suggestCommand(String command) {
        if (!command.startsWith("/")) command = "/" + command;
        if (!command.endsWith(" ")) command = command + " ";

        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));

        return this;
    }

    /**
     * Sends this TextComponent to player.
     * @param player Target player.
     */
    public void send(Player player){
        player.spigot().sendMessage(component);
    }

    /**
     * Sends this TextComponent to multiple players.
     * @param players Target players.
     */
    public void send(Player... players) {
        for (Player player : players) {
            player.spigot().sendMessage(component);
        }
    }

    /**
     * Broadcasts this TextComponent to all players.
     */
    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(player ->
                player.spigot().sendMessage(component));
    }

    /**
     * Sends this TextComponent to Scoreboard team.
     * @param team Target team.
     */
    public void send(Team team) {
        team.getEntries().forEach(entry -> {
            if (Bukkit.getPlayer(entry) != null) {
                send(Objects.requireNonNull(Bukkit.getPlayer(entry)));
            }
        });
    }

    /**
     * @return {@link TextComponent}
     */
    public TextComponent getComponent() {
        return component;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}