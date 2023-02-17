package com.github.tezvn.aradite.weapon.knife;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.language.Placeholder;
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
        Language lang = Aradite.getInstance().getLanguage();
        return lang.getListWithPlaceholders("lore.knife",
                Placeholder.of("damage", getMeta().getDamage() + ""));
    }
}
