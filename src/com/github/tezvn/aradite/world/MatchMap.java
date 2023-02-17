package com.github.tezvn.aradite.world;

import com.google.common.collect.Maps;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A class that manages all locations of the match.
 */
public class MatchMap {

    private Map<MatchLocationType, List<Location>> locationMap = Maps.newHashMap();

    public MatchMap(MapType mapType) {
        this.map = mapType;
    }

    private MapType map;

    /**
     * Return the representing map.
     */
    public MapType getMapType() {
        return map;
    }

    /**
     * Remove all locations whose type is {@code type}
     *
     * @param type Location type
     */
    public void unregister(MatchLocationType type) {
        this.locationMap.remove(type);
    }

    /**
     * Return {@code true} if the given {@code locationType} has been registered.
     * {@code false} otherwise.
     *
     * @param locationType Location type
     */
    public boolean hasRegistered(MatchLocationType locationType) {
        return locationMap.containsKey(locationType);
    }

    /**
     * Return the situations basing on the given {@code locationType}.
     *
     * @param locationType The location type.
     */
    public List<Location> getLocation(MatchLocationType locationType) {
        return locationMap.get(locationType);
    }

    /**
     * Set up the situations for the given {@code locationType}
     *
     * @param locationType The type of location
     * @param locations    The locations
     */
    public void registerLocation(MatchLocationType locationType, Location... locations) {
        if (!this.locationMap.containsKey(locationType))
            this.locationMap.put(locationType, new ArrayList<>(Arrays.asList(locations)));
        else {
            List<Location> locationList = locationMap.get(locationType);
            locationList.addAll(Arrays.asList(locations));
            this.locationMap.put(locationType, locationList);
        }
    }
}
