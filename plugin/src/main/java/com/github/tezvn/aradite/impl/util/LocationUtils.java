package com.github.tezvn.aradite.impl.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LocationUtils {

    public static List<Player> getNearbyPlayers(Location location, double range) {
        return Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, range, range, range, entity -> entity instanceof Player)
                .stream().map(entity -> (Player) entity).collect(Collectors.toList());
    }

    public static List<Player> getNearbyPlayers(Location location, double range, int limit) {
        return getNearbyPlayers(location, range).stream().limit(limit).collect(Collectors.toList());
    }

    public static RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance,
                                                  double raySize, Predicate<Entity> filter) {
        /* ... Precondition checks ... */
        Vector startPos = start.toVector();
        Vector dir = direction.clone().normalize().multiply(maxDistance);
        BoundingBox aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
        Collection<Entity> entities = Objects.requireNonNull(start.getWorld()).getNearbyEntities(aabb, filter);  // <---

        Entity nearestHitEntity = null;
        RayTraceResult nearestHitResult = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (Entity entity : entities) {
            BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
            RayTraceResult hitResult = boundingBox.rayTrace(startPos, direction, maxDistance);

            if (hitResult != null) {
                double distanceSq = startPos.distanceSquared(hitResult.getHitPosition());

                if (distanceSq < nearestDistanceSq) {
                    nearestHitEntity = entity;
                    nearestHitResult = hitResult;
                    nearestDistanceSq = distanceSq;
                }
            }
        }

        return nearestHitResult;
    }

}
