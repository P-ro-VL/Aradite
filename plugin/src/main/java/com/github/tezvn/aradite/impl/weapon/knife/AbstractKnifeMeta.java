package com.github.tezvn.aradite.impl.weapon.knife;

import com.github.tezvn.aradite.api.weapon.WeaponSkin;
import com.github.tezvn.aradite.api.weapon.knife.KnifeMeta;

import java.util.ArrayList;
import java.util.List;

public class AbstractKnifeMeta implements KnifeMeta {

    private List<WeaponSkin> skins = new ArrayList<>();
    private double damage = KnifeMeta.DEFAULT_DAMAGE;

    @Override
    public List<WeaponSkin> getSkins() {
        return skins;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public void setDamage(double damage) {
        this.damage = damage;
    }
}
