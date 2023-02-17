package com.github.tezvn.aradite.weapon.gun.type;

import com.github.tezvn.aradite.weapon.BodyPart;
import com.github.tezvn.aradite.weapon.gun.AbstractGun;
import com.github.tezvn.aradite.weapon.gun.GunType;
import com.github.tezvn.aradite.weapon.gun.meta.AbstractGunMeta;
import com.github.tezvn.aradite.weapon.gun.meta.GunMeta;
import com.github.tezvn.aradite.weapon.gun.meta.GunMetaType;
import com.github.tezvn.aradite.weapon.gun.meta.WallPenetration;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import pdx.mantlecore.java.IntRange;

import java.util.Map;

public class Bucky extends AbstractGun {
    public Bucky() {
        super("BUCKY", "Bucky", GunType.SHORTGUN);
        setScopable(false);

        GunMeta meta = new AbstractGunMeta();

        Map<GunMetaType, Double> attributeMap = Maps.newHashMap();

        attributeMap.put(GunMetaType.AE, 850d);
        attributeMap.put(GunMetaType.FIRE_RATE, 1.1);
        attributeMap.put(GunMetaType.RUN_SPEED, 5.06);
        attributeMap.put(GunMetaType.EQUIP_SPEED, 1.0);
        attributeMap.put(GunMetaType.RELOAD_SPEED, 2.5);
        attributeMap.put(GunMetaType.MAGAZINE, 5d);
        attributeMap.put(GunMetaType.RESERVE, 2d);
        attributeMap.put(GunMetaType.WALL_PENETRATION, (double) WallPenetration.NONE.ordinal());
        attributeMap.put(GunMetaType.SOUND_RANGE, 15d);
        meta.setAttribute(attributeMap);

        Table<IntRange, BodyPart, Integer> partDamage = HashBasedTable.create();

        IntRange firstRange = IntRange.of(0, 8);
        partDamage.put(firstRange, BodyPart.HEAD, 3);
        partDamage.put(firstRange, BodyPart.BODY, 5);
        partDamage.put(firstRange, BodyPart.LEGS, 7);

        IntRange secondRange = IntRange.of(8, 16);
        partDamage.put(secondRange, BodyPart.HEAD, 1);
        partDamage.put(secondRange, BodyPart.BODY, 2);
        partDamage.put(secondRange, BodyPart.LEGS, 1);

        IntRange thirdRange = IntRange.of(16, 20);
        partDamage.put(thirdRange, BodyPart.HEAD, 1);
        partDamage.put(thirdRange, BodyPart.BODY, 2);
        partDamage.put(thirdRange, BodyPart.LEGS, 2);

        IntRange fourthRange = IntRange.of(20, 50);
        partDamage.put(fourthRange, BodyPart.HEAD, 2);
        partDamage.put(fourthRange, BodyPart.BODY, 3);
        partDamage.put(fourthRange, BodyPart.LEGS, 3);

        meta.setDamageTable(partDamage);

        setMeta(meta);
    }

    @Override
    public int getCustomModelData() {
        return 2000001;
    }
}
