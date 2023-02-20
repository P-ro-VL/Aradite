package com.github.tezvn.aradite.api.team;

import com.github.tezvn.aradite.api.agent.Agent;
import com.github.tezvn.aradite.api.team.type.UndefinedTeam;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MatchTeam {

    /**
     * Reset the team data for new matches.
     */
    void clear();

    /**
     * Define which team will become the attacker team after 3 first rounds.
     */
    void defineAttacker(UndefinedTeam.Type teamType);

    /**
     * Select an agent for the given player.
     *
     * @param player The player
     * @param agent  The agent he picked.
     */
    void setSelectedAgents(Player player, Agent agent);

    /**
     * Return the representing color of the team that the given player in.
     *
     * @param player The player
     * @return The color of player's team
     */
    String getTeamColor(Player player);

    /**
     * A {@link Map} contains agents picked by players in agent select phase.
     */
    Map<UUID, Agent> getSelectedAgents();

    /**
     * Return a {@link List} containing all players of two teams and observers.
     */
    List<Player> getPlayersAndObservers();

    /**
     * Return a {@link List} containing all players of two teams.
     */
    List<Player> getAllPlayers();

    /**
     * Return a team that the given {@code player} is currently in.
     */
    Team getTeamOf(Player player);

    Team getTeam(TeamRole role);

    /**
     * Return the type of team that the given player is in.
     *
     * @param player Player to check
     * @return Player's team type
     */
    TeamRole getPlayerTeam(Player player);

    /**
     * Check if these two players are on the same team.
     *
     * @param player   Player A
     * @param comparer Player B
     * @return {@code true} if they are on the same team, {@code false} otherwise.
     */
    boolean isOnSameTeam(Player player, Player comparer);

    /**
     * Define a team. This method can only be called when a game is started.
     *
     * @param role The role of the team.
     * @param team The team
     */
    void defineTeam(TeamRole role, Team team);

    /**
     * Return the boss bar of the given player.
     *
     * @param player The player
     */
    BossBar getBossBar(Player player);

    /**
     * Update the title, color and style of all players' boss bars.<br>
     * Apart from the title, the others are nullable (no changes will be made).
     * If the {@code progress} is getOpposite in [0,1], the bar's progress will getOpposite be affected.
     *
     * @param newTitle New title
     * @param color    New color
     * @param style    New style
     * @param progress New progress
     */
    void updateAllBossbars(String newTitle, BarColor color, BarStyle style, double progress);

    /**
     * Broadcast a message to all players in the match.
     *
     * @param broadcastType Type of broadcast
     * @param message       The message
     */
    void broadcast(BroadcastType broadcastType, String message);

    /**
     * Send the title to all players in the match.
     *
     * @param title The title
     */
    void broadcastTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut);

    /**
     * Return the Player Of the Game. (Player who has the highest mvp point among all players).
     */
    Map.Entry<Player, Double> getPOG();

    /**
     * Return the MVP player of the given team.
     *
     * @param team The team
     * @return The MVP
     */
    Map.Entry<Player, Double> getMVPOf(TeamRole team);

    /**
     * Return {@code true} if player has died in the match, {@code false} otherwise.
     *
     * @param player The player
     */
    boolean isDead(Player player);

    enum BroadcastType {
        NORMAL_CHAT,
        CENTER_CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE
    }

}
