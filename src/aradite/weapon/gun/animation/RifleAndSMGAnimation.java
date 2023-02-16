package aradite.weapon.gun.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pdx.mantlecore.math.Shapes;

import java.util.ArrayList;
import java.util.List;

public class RifleAndSMGAnimation implements ShootAnimation{
    @Override
    public List<Location> calculate(Player player, int range) {
        return new ArrayList<>(Shapes.lineFromPlayer(player, range));
    }
}
