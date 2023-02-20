package com.github.tezvn.aradite.api.match;

import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBar;
import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBarType;
import com.github.tezvn.aradite.api.data.log.Report;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameData;
import com.github.tezvn.aradite.api.task.MatchTask;
import com.github.tezvn.aradite.api.team.MatchTeam;
import com.github.tezvn.aradite.api.world.MatchMap;
import com.github.tezvn.aradite.api.match.mechanic.Mechanic;
import com.github.tezvn.aradite.api.match.mechanic.MechanicType;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;

import java.util.List;

public interface Match {

    /**
     * Return the status bars table.
     */
    Table<Player, StatusBarType, StatusBar> getStatusBars();

    /**
     * Change the map's uuid
     *
     * @param uuid New uuid
     */
    void setUuid(String uuid);

    /**
     * Return the score manager of the match.
     */
    MatchScore getMatchScore();

    /**
     * Set up the flag value for the match.
     *
     * @param flag  The flag
     * @param value The value
     */
    void setupFlag(MatchFlag flag, boolean value);

    /**
     * Return the current value of the given {@code flag}.
     */
    boolean getFlag(MatchFlag flag);

    /**
     * Return the currently running mechanic.
     */
    Mechanic getCurrentMechanic();

    /**
     * Change the current mechanic.<br>
     * This method can getOpposite be separately run but through
     *
     * @param currentMechanic New current mechanic.
     */
    void setCurrentMechanic(Mechanic currentMechanic);

    /**
     * Return the map of the match.
     */
    MatchMap getMatchMap();

    /**
     * Return the type of the match.
     *
     * @see MatchType
     */
    MatchType getMatchType();

    /**
     * Return all players' ingame packets.
     */
    List<PlayerInGameData> getProtocols();

    /**
     * Return the ingame packet of specific {@link Player} whose uuid is
     * {@code uuid}.
     */
    PlayerInGameData retrieveProtocol(Player player);

    /**
     * Return the task manager of the match.
     */
    MatchTask getMatchTask();

    /**
     * Return the report of the match.
     */
    Report getReport();

    /**
     * Return the match's team controller.
     */
    MatchTeam getMatchTeam();

    /**
     * Return the {@code ID} of the game.<br>
     * The UUID will be automatically generated whenever a game starts.
     */
    String getUniqueID();

    /**
     * Return the current phase of the game.
     */
    MatchPhase getPhase();

    /**
     * Change the current phase of the game.
     */
    void setPhase(MatchPhase phase);

    /**
     * Return all players who are waiting for the game to start.
     */
    List<Player> getWaitingPlayers();

    /**
     * Force a player to join the game.
     *
     * @param player Player to be forced
     * @return {@code true} if joining successfully, {@code false} otherwise.
     */
    boolean join(Player player);

    /**
     * Force to start the match.
     */
    void start();

    /**
     * Setup status bars for players.
     */
    void setupStatusBars();

    /**
     * Set up data and ingame data packet for all waiting players.
     */
    void setupProtocol();

    /**
     * Categorize players who are waiting in the match into two different undefined
     * teams.
     */
    void shuffle();

    /**
     * Start a game mechanic.<bR>
     * This method is usually called to start the actual game or change mechanic when the last one has finished.
     *
     * @param mechanicType Type of mechanic to start
     */
    void runMechanic(MechanicType mechanicType, int index);

    /**
     * Finish the match.
     */
    void finish();

}
