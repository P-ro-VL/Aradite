package com.github.tezvn.aradite.impl.weapon.gun.type;

import com.github.tezvn.aradite.api.weapon.BodyPart;
import com.github.tezvn.aradite.api.weapon.gun.GunType;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMeta;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMetaType;
import com.github.tezvn.aradite.api.weapon.gun.meta.WallPenetration;
import com.github.tezvn.aradite.impl.weapon.gun.AbstractGun;
import com.github.tezvn.aradite.impl.weapon.gun.meta.AbstractGunMeta;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import pdx.mantlecore.java.IntRange;

import java.util.Map;

public class Bucky extends AbstractGun {
    public Bucky() {
        super("bucky", "Bucky", GunType.SHORTGUN);
        setScopable(false);

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
        getMeta().setAttribute(attributeMap);

        Table<IntRange, BodyPart, Integer> partDamage = HashBasedTable.create();

        IntRange firstRange = IntRange.of(0, 8);
        partDamage.put(firstRange, BodyPart.HEAD, 3);
        partDamage.put(firstRange, BodyPart.BODY, 5);
        partDamage.put(firstRange, BodyPart.LEGS, 7);
        partDamage.put(firstRange, BodyPart.HANDS, 8);

        IntRange secondRange = IntRange.of(8, 16);
        partDamage.put(secondRange, BodyPart.HEAD, 1);
        partDamage.put(secondRange, BodyPart.BODY, 2);
        partDamage.put(secondRange, BodyPart.LEGS, 1);
        partDamage.put(firstRange, BodyPart.HANDS, 8);

        IntRange thirdRange = IntRange.of(16, 20);
        partDamage.put(thirdRange, BodyPart.HEAD, 1);
        partDamage.put(thirdRange, BodyPart.BODY, 2);
        partDamage.put(thirdRange, BodyPart.LEGS, 2);
        partDamage.put(firstRange, BodyPart.HANDS, 8);

        IntRange fourthRange = IntRange.of(20, 50);
        partDamage.put(fourthRange, BodyPart.HEAD, 2);
        partDamage.put(fourthRange, BodyPart.BODY, 3);
        partDamage.put(fourthRange, BodyPart.LEGS, 3);
        partDamage.put(firstRange, BodyPart.HANDS, 8);

        getMeta().setDamageTable(partDamage);

    }

    @Override
    public int getCustomModelData() {
        return 2000001;
    }
}
