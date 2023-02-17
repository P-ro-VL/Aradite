package com.github.tezvn.aradite.weapon;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.java.collection.Lists;

import java.util.List;

public enum WeaponType {

    MAIN,
    SUB;

    private Language lang = Aradite.getInstance().getLanguage();

    /**
     * Return the display name of the weapon type.
     */
    public String getDisplay() {
        return lang.getString("weapon." + toString().toLowerCase());
    }

    /**
     * Return the description of the weapon type.
     */
    public List<String> getDescription() {
        return Lists.newArrayList(ChatPaginator.wordWrap(lang.getString("weapon.description."
                + toString().toLowerCase()), 30));
    }
}
