package com.github.tezvn.aradite.api.weapon;

import com.github.tezvn.aradite.api.AraditeAPIProvider;
import com.github.tezvn.aradite.api.language.Language;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.java.collection.Lists;

import java.util.List;

public enum WeaponType {

    MAIN,
    SUB;

    /**
     * Return the display name of the weapon type.
     */
    public String getDisplay() {
        return getLanguage().getString("weapon." + toString().toLowerCase());
    }

    /**
     * Return the description of the weapon type.
     */
    public List<String> getDescription() {
        return Lists.newArrayList(ChatPaginator.wordWrap(getLanguage().getString("weapon.description."
                + toString().toLowerCase()), 30));
    }

    private Language getLanguage() {
        return AraditeAPIProvider.get().getLanguage();
    }
}
