package com.github.tezvn.aradite.impl.weapon.knife.type;

import com.github.tezvn.aradite.api.AraditeAPIProvider;
import com.github.tezvn.aradite.api.weapon.knife.type.Dagger;
import com.github.tezvn.aradite.impl.weapon.knife.AbstractKnife;
import org.bukkit.Material;

public class DaggerImpl extends AbstractKnife implements Dagger {

    public DaggerImpl() {
        super("dagger", AraditeAPIProvider.get().getLanguage()
                .getString("weapon.displayname.knife.dagger"), Material.STONE_SWORD);
    }

    @Override
    public int getCustomModelData() {
        return 9999901;
    }
}
