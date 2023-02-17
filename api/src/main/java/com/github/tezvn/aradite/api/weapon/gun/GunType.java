package com.github.tezvn.aradite.api.weapon.gun;

import com.github.tezvn.aradite.impl.AraditeImpl;

public enum GunType {

    SHORTGUN(8, ShootMode.SHORTGUN),

    RIFLE(30, ShootMode.RIFLE_AND_SMG),

    SNIPER(42, ShootMode.SNIPER),

    SMG(25, ShootMode.RIFLE_AND_SMG),

    SIDEARM(25, ShootMode.RIFLE_AND_SMG),

    MACHINE_GUN(30, ShootMode.RIFLE_AND_SMG);

    private final ShootMode shootMode;
    private int range;

    private GunType(int range, ShootMode shootMode) {
        this.range = range;
        this.shootMode = shootMode;
    }

    /**
     * Return the particle shoot mode.
     */
    public ShootMode getShootMode() {
        return shootMode;
    }

    /**
     * Return the maximum range the bullets can reach.
     */
    public int getRange() {
        return range;
    }

    /**
     * Return the display name of the gun type.
     */
    public String getDisplayName() {
        return AraditeImpl.getInstance().getLanguage().getString("gun-type." + this.toString().toLowerCase());
    }

}
