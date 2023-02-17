package com.github.tezvn.aradite.impl.weapon.gun.meta;

import com.github.tezvn.aradite.api.weapon.BodyPart;
import com.github.tezvn.aradite.api.weapon.WeaponSkin;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMeta;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMetaType;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import pdx.mantlecore.java.IntRange;
import pdx.mantlecore.java.collection.Lists;

import java.util.List;
import java.util.Map;

public class AbstractGunMeta implements GunMeta {

    private Table<IntRange, BodyPart, Integer> damageTable = HashBasedTable.create();
    private List<WeaponSkin> skins = Lists.newArrayList();
    private Map<GunMetaType, Double> attribute = Maps.newHashMap();

    @Override
    public List<WeaponSkin> getSkins() {
        return this.skins;
    }

    @Override
    public Table<IntRange, BodyPart, Integer> getDamageTable() {
        return this.damageTable;
    }

    @Override
    public void setDamageTable(Table<IntRange, BodyPart, Integer> damageTable) {
        this.damageTable = damageTable;
    }

    @Override
    public Map<GunMetaType, Double> getAttribute() {
        return this.attribute;
    }

    @Override
    public void setAttribute(GunMetaType attribute, double value) {
        this.attribute.put(attribute, value);
    }

    @Override
    public void setAttribute(Map<GunMetaType, Double> attribute) {
        this.attribute.putAll(attribute);
    }
}
