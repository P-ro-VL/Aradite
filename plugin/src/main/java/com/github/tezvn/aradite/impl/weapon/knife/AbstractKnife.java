package com.github.tezvn.aradite.impl.weapon.knife;

import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.weapon.knife.Knife;
import com.github.tezvn.aradite.api.weapon.knife.KnifeMeta;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.language.Placeholder;
import org.bukkit.Material;

import java.util.List;

public abstract class AbstractKnife implements Knife {

    private String ID;
    private Material material;
    private String displayName;
    private double damage = 5;
    private KnifeMeta meta;

    public AbstractKnife(String ID, String displayname, Material material) {
        this.ID = ID;
        this.displayName = displayname;
        this.material = material;
        this.meta = new AbstractKnifeMeta();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public KnifeMeta getMeta() {
        return meta;
    }

    public void setMeta(KnifeMeta meta) {
        this.meta = meta;
    }

    /**
     * Return the damage the knife deals.
     */
    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public List<String> getLore() {
        Language lang = AraditeImpl.getInstance().getLanguage();
        return lang.getListWithPlaceholders("lore.knife",
                Placeholder.of("damage", getMeta().getDamage() + ""));
    }
}