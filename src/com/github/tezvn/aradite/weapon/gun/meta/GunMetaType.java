package com.github.tezvn.aradite.weapon.gun.meta;

public enum GunMetaType {

    AE,
    WALL_PENETRATION(true),
    SOUND_RANGE,
    FIRE_RATE(true),
    RUN_SPEED,
    EQUIP_SPEED,
    RELOAD_SPEED(true),
    MAGAZINE(true),
    RESERVE(true),
    ALT_FIRE_RATE,
    ALT_MOVE_SPEED;

    private final boolean displayable;

    private GunMetaType() {
        this.displayable = false;
    }

    private GunMetaType(boolean displayable) {
        this.displayable = displayable;
    }

    /**
     * Return {@code true} if the attribute can be displayed in the lore of the gun.
     */
    public boolean isDisplayable() {
        return displayable;
    }
}
