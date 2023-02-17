package com.github.tezvn.aradite.api.weapon.gun.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Particles shot by the guns.
 */
public interface ShootAnimation {

    /**
     * Calculate all locations to display particles.
     * @param player The shooter.
     * @param range The maximum distance the bullets can travel.
     */
    public List<Location> calculate(Player player, int range);

}
