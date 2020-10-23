package com.l3tplay.liteconomy.utils;

import org.bukkit.ChatColor;

import java.util.List;

public class ColorUtils {

    public static String colorString(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> colorList(List<String> list) {
        list.replaceAll(ColorUtils::colorString);
        return list;
    }
}
