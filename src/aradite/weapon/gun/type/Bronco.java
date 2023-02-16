package aradite.weapon.gun.type;

import aradite.nms.recoil.ZoomRatio;
import aradite.weapon.BodyPart;
import aradite.weapon.WeaponSkinEdition;
import aradite.weapon.gun.*;
import aradite.weapon.gun.meta.AbstractGunMeta;
import aradite.weapon.gun.meta.GunMeta;
import aradite.weapon.gun.meta.GunMetaType;
import aradite.weapon.gun.meta.WallPenetration;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.bukkit.Material;

import pdx.mantlecore.java.IntRange;
import pdx.mantlecore.message.GradientText;

import java.util.Map;

public class Bronco extends AbstractGun {

    public Bronco() {
        super("Bronco", "§7Bronco", GunType.SMG);

        setScopable(true);
        setScopeMode(ZoomRatio.SMALL);

        GunMeta meta = new AbstractGunMeta();
        meta.registerSkin("sakura", GradientText.toGradient("Bronco Sakura", "&d", "&f")
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
        meta.setAttribute(attributeMap);

        Table<IntRange, BodyPart, Integer> partDamage = HashBasedTable.create();

        IntRange firstRange = IntRange.of(0, 20);
        partDamage.put(firstRange, BodyPart.HEAD, 2);
        partDamage.put(firstRange, BodyPart.BODY, 4);
        partDamage.put(firstRange, BodyPart.LEGS, 5);

        IntRange secondRange = IntRange.of(20, 50);
        partDamage.put(secondRange, BodyPart.HEAD, 2);
        partDamage.put(secondRange, BodyPart.BODY, 4);
        partDamage.put(secondRange, BodyPart.LEGS, 5);

        meta.setDamageTable(partDamage);

        setMeta(meta);
    }


    @Override
    public int getCustomModelData() {
        return 1000001;
    }
}
