package com.github.tezvn.aradite.impl.weapon.gun.animation;

import com.github.tezvn.aradite.api.weapon.gun.animation.ShootAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pdx.mantlecore.math.Shapes;

import java.util.ArrayList;
import java.util.List;

public class SniperAnimation implements ShootAnimation {

    @Override
    public List<Location> calculate(Player player, int range) {
        return new ArrayList<>(Shapes.lineFromPlayer(player, range));
    }

}
