package com.github.tezvn.aradite.api.match;

import org.bukkit.Material;

public enum MatchType {
    NORMAL(Material.LIME_WOOL),
    RANKED(Material.YELLOW_WOOL),
    PRACTICE(Material.BROWN_WOOL),
    CUSTOM(Material.WHITE_WOOL),
    SPECIAL_MODE(Material.MAGENTA_WOOL);

    private final Material icon;

    private MatchType(Material icon){
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }
}
