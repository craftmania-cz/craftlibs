package cz.craftmania.craftlibs.utils;

import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;

public enum ServerColors {

    // Basic
    CRAFTMANIA_BLUE("#3EB9C7"),

    // Roles
    ROLE_OWNER("#3d7eff"),
    ROLE_MANAGER("#d61367"),
    ROLE_ADMIN("#e44650"),
    ROLE_HELPER("#43ca62"),
    ROLE_DEVELOPER("#fcc931"),
    ROLE_EVENTER("#ff6eb4"),
    ROLE_BUILDER("#566fb8"),
    ROLE_ARTIST("#cea672"),
    ROLE_MODERATOR("#f78811"),
    ROLE_TESTER("#8A0B50"),
    ROLE_OBSIDIAN("#8953ff"),
    ROLE_EMERALD("#35c48c"),
    ROLE_DIAMOND("#87cefa"),
    ROLE_GOLD("#e7b128"),

    // Servers
    SERVER_SURVIVAL("#00CDCD"),
    SERVER_SKYBLOCK("#3D9140"),
    SERVER_CREATIVE("#F4A460"),
    SERVER_VANILLA_LANDS("#EEC900"),
    SERVER_VANILLA_ANARCHY("#CD0000"),
    SERVER_PRISON("#9340c2"),
    SERVER_EVENT("#EE30A7"),
    SERVER_LOBBY("#4876FF"),

    // Messages
    INFO("#FCC225"),
    DANGER("#FF474F"),
    SUCCESS("#30A22C"),

    // Plugins
    ECONOMY("#007A58"),
    SERVER("#5882b4"),

    // Others
    DARK_GRAY("#7D7D7D");

    private final String hexColor;

    ServerColors(String hexColor){
        this.hexColor = hexColor;
    }

    @Deprecated
    public ChatColor getChatColor() {
        return ChatColor.of(this.hexColor);
    }

    public String getHexColor() {
        return this.hexColor;
    }

    public TextColor getTextColor() {
        return TextColor.fromHexString(this.hexColor);
    }
}
