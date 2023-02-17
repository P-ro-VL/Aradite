package com.github.tezvn.aradite.api.team.type;

import com.github.tezvn.aradite.api.team.Team;
import org.bukkit.entity.Player;

import java.util.List;

public interface UndefinedTeam extends Team {

    /**
     * Add a player into a team.
     *
     * @param player Player
     * @param type   Team
     */
    void addPlayer(Player player, UndefinedTeam.Type type);

    @Override
    @Deprecated
    void addMember(Player player);

    /**
     * Return all players in the given team's {@code type}
     *
     * @param type Type of the team.
     */
    List<Player> getPlayerInTeam(UndefinedTeam.Type type);

    @Override
    List<Player> getMembers();

    /**
     * Return all players in the team.
     */
    List<Player> getPlayers();

    /**
     * Return the team that the given player is in.
     *
     * @param player The player
     * @return The team he is in.
     */
    UndefinedTeam.Type getTeamOf(Player player);

    public static enum Type {
        A, B;

        public static UndefinedTeam.Type getOpposite(Type type) {
            return type == A ? B : A;
        }
    }
}
