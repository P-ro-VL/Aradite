package com.github.tezvn.aradite.impl.weapon.gun.type;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.weapon.BodyPart;
import com.github.tezvn.aradite.api.weapon.WeaponSkinEdition;
import com.github.tezvn.aradite.api.weapon.gun.GunType;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMeta;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMetaType;
import com.github.tezvn.aradite.api.weapon.gun.meta.WallPenetration;
import com.github.tezvn.aradite.api.recoil.ZoomRatio;
import com.github.tezvn.aradite.impl.weapon.gun.AbstractGun;
import com.github.tezvn.aradite.impl.weapon.gun.meta.AbstractGunMeta;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import pdx.mantlecore.java.IntRange;
import pdx.mantlecore.message.GradientText;

import java.util.Map;

public class Bronco extends AbstractGun {

    public Bronco() {
        super("bronco", "§7Bronco", GunType.SMG);

        setScopable(true);
        setScopeMode(ZoomRatio.SMALL);

        getMeta().registerSkin("sakura", GradientText.toGradient("Bronco Sakura", "&d", "&f")
                ,1000002, WeaponSkinEdition.PREMIUM, -1, false);

        Map<GunMetaType, Double> attributeMap = Maps.newHashMap();

        attributeMap.put(GunMetaType.AE, 950d);
        attributeMap.put(GunMetaType.FIRE_RATE, 16d);
        attributeMap.put(GunMetaType.RUN_SPEED, 5.73);
        attributeMap.put(GunMetaType.EQUIP_SPEED, 0.75);
        attributeMap.put(GunMetaType.RELOAD_SPEED, 2.25);
        attributeMap.put(GunMetaType.MAGAZINE, 20d);
        attributeMap.put(GunMetaType.RESERVE, 3d);
        attributeMap.put(GunMetaType.ALT_FIRE_RATE, 52D);
        attributeMap.put(GunMetaType.ALT_MOVE_SPEED, 76d);
        attributeMap.put(GunMetaType.WALL_PENETRATION, (double) WallPenetration.LOW.ordinal());
        attributeMap.put(GunMetaType.SOUND_RANGE, 10d);
        getMeta().setAttribute(attributeMap);

        Table<IntRange, BodyPart, Integer> partDamage = HashBasedTable.create();

        IntRange firstRange = IntRange.of(0, 20);
        partDamage.put(firstRange, BodyPart.HEAD, 2);
        partDamage.put(firstRange, BodyPart.BODY, 4);
        partDamage.put(firstRange, BodyPart.LEGS, 5);
        partDamage.put(firstRange, BodyPart.HANDS, 8);

        IntRange secondRange = IntRange.of(20, 50);
        partDamage.put(secondRange, BodyPart.HEAD, 2);
        partDamage.put(secondRange, BodyPart.BODY, 4);
        partDamage.put(secondRange, BodyPart.LEGS, 5);
        partDamage.put(secondRange, BodyPart.HANDS, 8);

        getMeta().setDamageTable(partDamage);

    }


    @Override
    public int getCustomModelData() {
        return 1000001;
    }

}
