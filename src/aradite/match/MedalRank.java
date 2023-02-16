package aradite.match;

import aradite.Aradite;

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
        return Aradite.getInstance().getLanguage().getString("medal.rank." + toString());
    }
}
