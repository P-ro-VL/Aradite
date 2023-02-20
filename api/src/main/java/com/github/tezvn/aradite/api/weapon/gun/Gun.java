package com.github.tezvn.aradite.api.weapon.gun;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.recoil.ZoomRatio;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.api.weapon.WeaponCategory;
import com.github.tezvn.aradite.api.weapon.WeaponType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface Gun extends Weapon {

    NamespacedKey ID_KEY = new NamespacedKey("aradite", "weapon_id");

    /**
     * Return the type of the gun.
     */
    GunType getType();

    /**
     * Return {@code true} if this gun has a scoping mode.
     */
    boolean isScopable();

    /**
     * Return the zoom ratio of the gun if this gun is {@link #isScopable() scopable}.
     */
    ZoomRatio getScopeMode();

    @Override
    default boolean isMelee() {
        return false;
    }

    @Override
    default WeaponType getWeaponType() {
        return WeaponType.MAIN;
    }

    @Override
    default WeaponCategory getCategory() {
        return WeaponCategory.GUN;
    }

    void shoot(Match match, Player player, boolean isInScopingMode);

}
