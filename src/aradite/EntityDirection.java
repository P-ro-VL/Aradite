package aradite;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

/**
 * A class that defines the direction the {@link LivingEntity} is forwarding.
 */
public final class EntityDirection {

    private final Location from;
    private final Location to;
    private final LivingEntity entity;

    public EntityDirection(LivingEntity entity, Location from, Location to) {
        this.from = from;
        this.to = to;
        this.entity = entity;
    }

    /**
     * Return the direction the entity is forwarding.
     */
    public Direction getMovingDirection() {
        Vector direction = entity.getEyeLocation().getDirection();
        Vector move = to.toVector().subtract(from.toVector());

        Vector crossProductVector = direction.crossProduct(move);
        if (!crossProductVector.equals(new Vector(0, 0, 0))) {
            Location entityLocation = entity.getEyeLocation();

            double x = entityLocation.getX(), y = entityLocation.getY(), z = entityLocation.getZ();

            Location headLocation = entityLocation.clone().add(0, 2, 0);
            Vector secondDirection = entityLocation.toVector().subtract(headLocation.toVector());

            Vector vtpt = secondDirection.crossProduct(direction);
            double c = vtpt.getX() * (-x) + vtpt.getY() * (-y) + vtpt.getZ() * (-z);

            double t1 = calculateT(vtpt.getX(), vtpt.getY(), vtpt.getZ(), c, from.getX(), from.getY(), from.getZ());
            double t2 = calculateT(vtpt.getX(), vtpt.getY(), vtpt.getZ(), c, to.getX(), to.getY(), to.getZ());

            double hinhChieu1X = hinhChieu(t1, vtpt.getX(), from.getX());
            double hinhChieu1Y = hinhChieu(t1, vtpt.getY(), from.getY());
            double hinhChieu1Z = hinhChieu(t1, vtpt.getZ(), from.getZ());
            Vector hinhChieu1 = new Vector(hinhChieu1X, hinhChieu1Y, hinhChieu1Z);

            double hinhChieu2X = hinhChieu(t2, vtpt.getX(), to.getX());
            double hinhChieu2Y = hinhChieu(t2, vtpt.getY(), to.getY());
            double hinhChieu2Z = hinhChieu(t2, vtpt.getZ(), to.getZ());
            Vector hinhChieu2 = new Vector(hinhChieu2X, hinhChieu2Y, hinhChieu2Z);

            move = hinhChieu2.subtract(hinhChieu1);
        }

        double angle = Math.toDegrees(move.angle(direction));
        if (angle == 0) return Direction.FORWARD;
        else if (angle == 180) return Direction.BACKWARD;
        else if (angle == 90) return Direction.LEFT;
        return Direction.RIGHT;
    }

    private double calculateT(double vtptX, double vtptY, double vtptZ, double c, double x, double y, double z) {
        return -(
                (vtptX * x + vtptY * y + vtptZ * z + c) / (x * x + y * y + z * z)
        );
    }

    private double hinhChieu(double t, double vtpt, double coor) {
        return t * vtpt + coor;
    }

    public static enum Direction {
        FORWARD,
        LEFT,
        RIGHT,
        BACKWARD;
    }

}
