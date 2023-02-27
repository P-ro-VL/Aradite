package com.github.tezvn.aradite.impl.match;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.MatchType;
import com.github.tezvn.aradite.api.team.MatchTeam;
import com.github.tezvn.aradite.api.world.MapType;
import com.github.tezvn.aradite.api.world.MatchLocationType;
import com.github.tezvn.aradite.api.world.MatchMap;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pdx.mantlecore.java.Splitter;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatchManager {

    public static final String DATA_DIRECTORY_PATH = "plugins/Aradite/match-data/";

    private final Map<String, MatchImpl> availableMatches = Maps.newConcurrentMap();

    /**
     * Return all available matches.
     */
    public Map<String, MatchImpl> getAvailableMatches() {
        return availableMatches;
    }

    /**
     * Return the match whose {@link UUID} is {@code uuid}.
     *
     * @param uuid Match's uuid.
     */
    public MatchImpl getMatch(String uuid) {
        return availableMatches.get(uuid);
    }

    /**
     * Return the match that the given player is in.
     *
     * @param player The Player
     * @return The match he is in.
     */
    public Match getMatch(Player player) {
        for (MatchImpl match : this.availableMatches.values()) {
            MatchTeam team = match.getMatchTeam();
            if (team.getTeamOf(player) != null)
                return match;
        }
        return null;
    }

    /**
     * Return all registered matches.
     */
    public Collection<MatchImpl> getAllAvailableMatches() {
        return this.availableMatches.values();
    }

    /**
     * Register a new match.
     *
     * @param match The match need registering.
     */
    public void registerMatch(Match match) {
        this.availableMatches.put(match.getUniqueID(), (MatchImpl) match);
    }

    /**
     * Load all matches from local database.
     */
    public void load() {
        File folder = new File(DATA_DIRECTORY_PATH);
        if (!folder.exists()) folder.mkdirs();
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return;
        for (File file : files) {
            if (file.isDirectory() || !file.getName().endsWith(".yml")) continue;

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            try {
                String id = config.getString("id");
                MatchType matchType = MatchType.valueOf(config.getString("match-type"));
                MapType mapType = MapType.valueOf(config.getString("match-map.map-type"));

                MatchImpl match = new MatchImpl(matchType, mapType);
                match.setUuid(id);

                MatchMap matchMap = match.getMatchMap();
                for (String path : config.getConfigurationSection("match-map.locations").getKeys(false)) {
                    MatchLocationType locationType = MatchLocationType.valueOf(path);
                    List<String> parsedLocations = config.getStringList("match-map.locations." + path);
                    parsedLocations.stream().map(string -> {
                        String[] splittedData = string.split(Pattern.quote(","));
                        return new Location(Bukkit.getWorld(splittedData[0]),
                                Double.parseDouble(splittedData[1]),
                                Double.parseDouble(splittedData[2]),
                                Double.parseDouble(splittedData[3]));
                    }).forEach(location -> {
                        matchMap.registerLocation(locationType, location);
                    });
                }

                registerMatch(match);
            } catch (Exception ex) {
                ex.printStackTrace();
                Bukkit.getLogger().severe("!!! CANNOT LOAD MATCH DATA FOR MATCH WHOSE UUID IS " + file.getName()
                        + " !!!");
            }
        }
    }

    /**
     * Save all matches' data into the local database.
     */
    public void save() {
        for (MatchImpl match : getAllAvailableMatches()) {
            try {
                File file = new File(DATA_DIRECTORY_PATH + match.getUniqueID() + ".yml");
                if (!file.exists())
                    file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("id", match.getUniqueID());
                config.set("match-type", match.getMatchType().toString());

                MatchMap matchMap = match.getMatchMap();
                config.set("match-map.map-type", matchMap.getMapType().toString());
                for (MatchLocationType locationType : MatchLocationType.values()) {
                    List<Location> locations = matchMap.getLocation(locationType);
                    List<String> parsedLocations = locations.stream().map(
                            location -> Splitter.newInstance().splitBy(",")
                                    .appendElements(location.getWorld().getName(), location.getX(), location.getY(),
                                            location.getZ()).toString()).collect(Collectors.toList());
                    config.set("match-map.locations." + locationType.toString(), parsedLocations);
                }

                config.save(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                Bukkit.getLogger().severe("!!! A SEVERE ERROR HAS OCCURED WHILE TRYING TO SAVE DATA " +
                        "FOR MATCH WHOSE UUID IS " + match.getUniqueID() + " !!!");
                continue;
            }
        }
    }

}
