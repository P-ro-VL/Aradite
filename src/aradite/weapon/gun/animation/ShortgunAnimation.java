package aradite.weapon.gun.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pdx.mantlecore.java.collection.Lists;
import pdx.mantlecore.math.Shapes;

import java.util.ArrayList;
import java.util.List;

public class ShortgunAnimation implements ShootAnimation {
    @Override
    public List<Location> calculate(Player player, int range) {
        List<Location> toReturn = Lists.newArrayList();
        List<Location> points = new ArrayList<>(Shapes.lineFromPlayer(player, range));
        points.forEach(center -> {
            toReturn.addAll(circle(center, 0.5));
            toReturn.addAll(circle(center, 0.85d));
            toReturn.addAll(circle(center, 1.7d));
        });
        return toReturn;
    }

    private List<Location> circle(Location center, double radius) {
        List<Location> list = new ArrayList<Location>();
        // Number of points in each circle
        int circlePoints = 8;
        Location playerLoc = center;
        // We need the pitch in radians for the rotate axis function
        // We also add 90 degrees to compensate for the non-standard use of pitch
        // degrees in Minecraft.
        final double pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
        // The yaw is also converted to radians here, but we need to negate it for the
        // function to work properly
        final double yaw = -playerLoc.getYaw() * 0.017453292F;
        // This is the distance between each point around the circumference of the
        // circle.
        double increment = (2 * Math.PI) / circlePoints;
        // We need to loop to get all of the points on the circle every loop
        for (int i = 0; i < circlePoints; i++) {
            double angle = i * increment;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            // Convert that to a 3D Vector where the height is always 0
            Vector vec = new Vector(x, 0, z);
            // Now rotate the circle point so it's properly aligned no matter where the
            // player is looking:
            vec = rotateAroundX(vec, pitch);
            vec = rotateAroundY(vec, yaw);
            // Add that vector to the player's current location
            // Add to list
            list.add(playerLoc.clone().add(vec));
            // Since add() modifies the original variable, we have to subtract() it so the
            // next calculation starts from the same location as this one.
        }
        return list;
    }

    private Vector rotateAroundX(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundY(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }
}
