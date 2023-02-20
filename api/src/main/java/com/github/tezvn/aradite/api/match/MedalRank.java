package com.github.tezvn.aradite.api.match;

import com.github.tezvn.aradite.api.AraditeAPIProvider;

/**
 * Ranks of match medals.
 */
public enum MedalRank {

    APPRECIATED("&a"),
    ELITE("&b"),
    MASTER("&d"),
    CHALLENGER("&6");

    private final String color;

    private MedalRank(String color) {
        this.color = color;
    }

    /**
     * Return the display color of the rank.
     */
    public String getColor() {
        return color.replace("&", "§");
    }

    /**
     * Return the rank display name.
     */
    public String getDisplayName() {
        return AraditeAPIProvider.get().getLanguage().getString("medal.rank." + toString());
    }
}
