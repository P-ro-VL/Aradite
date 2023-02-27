package com.github.tezvn.aradite.impl.weapon.gun.type;

import com.github.tezvn.aradite.api.recoil.ZoomRatio;
import com.github.tezvn.aradite.api.weapon.BodyPart;
import com.github.tezvn.aradite.api.weapon.gun.GunType;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMetaType;
import com.github.tezvn.aradite.api.weapon.gun.meta.WallPenetration;
import com.github.tezvn.aradite.api.weapon.gun.type.Operator;
import com.github.tezvn.aradite.impl.weapon.gun.AbstractGun;
import pdx.mantlecore.java.IntRange;

import java.util.Arrays;

public class OperatorImpl extends AbstractGun implements Operator {

    public OperatorImpl() {
        super("operator", "Operator", GunType.SNIPER);

        setScopable(true);
        setScopeMode(ZoomRatio.HUGE);

        for (GunMetaType type : GunMetaType.values()) {
            double value = -1;
            switch (type) {
                case AE:
                    value = 5100;
                    break;
                case WALL_PENETRATION:
                    value = WallPenetration.HIGH.ordinal();
                    break;
                case SOUND_RANGE:
                    value = 10;
                    break;
                case FIRE_RATE:
                    value = 32;
                    break;
                case RUN_SPEED:
                    value = 1;
                    break;
                case EQUIP_SPEED:
                    value = 20;
                    break;
                case RELOAD_SPEED:
                    value = 3;
                    break;
                case MAGAZINE:
                    value = 20;
                    break;
                case RESERVE:
                    value = 3;
                    break;
                case ALT_FIRE_RATE:
                    value = 52;
                    break;
                case ALT_MOVE_SPEED:
                    value = 76;
                    break;
            }
            if(value == -1)
                continue;
            getMeta().getAttribute().put(type, value);
        }
        IntRange range = IntRange.of(20, 50);
        Arrays.stream(BodyPart.values()).forEach(part -> getMeta().getDamageTable().put(range, part, 1));
    }

    @Override
    public int getCustomModelData() {
        return 2000001;
    }
}
