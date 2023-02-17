package com.github.tezvn.aradite.api.world;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface MatchMap {

    /**
     * Return the representing map.
     */
    MapType getMapType();

    /**
     * Remove all locations whose type is {@code type}
     *
     * @param type Location type
     */
    void unregister(MatchLocationType type);

    /**
     * Return {@code true} if the given {@code locationType} has been registered.
     * {@code false} otherwise.
     *
     * @param locationType Location type
     */
    boolean hasRegistered(MatchLocationType locationType);

    /**
     * Return the situations basing on the given {@code locationType}.
     *
     * @param locationType The location type.
     */
    List<Location> getLocation(MatchLocationType locationType);

    /**
     * Set up the situations for the given {@code locationType}
     *
     * @param locationType The type of location
     * @param locations    The locations
     */
    void registerLocation(MatchLocationType locationType, Location... locations);
    
}
