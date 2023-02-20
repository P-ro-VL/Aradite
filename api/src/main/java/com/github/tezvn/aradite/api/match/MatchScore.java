package com.github.tezvn.aradite.api.match;

import com.github.tezvn.aradite.api.packet.type.PlayerInGameMVPPacket.MVPStatistics;
import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.UndefinedTeam;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import pdx.mantlecore.java.Pair;

import java.util.Map;

public interface MatchScore {

    /**
     * Clear the data for new matches.
     */
    void clear();

    /**
     * Return the score table.<br>
     * The table can be visualized like this:<br>
     * |   1   |   2   | ... |  n   |<br>
     * Team A | true  | false | ... | true |<br>
     * Team B | true  | true  | ... | false |<br>
     * <t>• Row 1 : Round</t><br>
     * <t>• Column 1 : The team</t><br>
     * <t>• {@code true/false} values: true is win, false is lost</t>
     */
    Table<Integer, TeamRole, Boolean> getScoreTable();

    /**
     * Return the initialized medal map.
     */
    Multimap<Player, Pair<MVPStatistics, MedalRank>> getMedals();

    /**
     * Return the result of the given undefined team
     * @param teamType
     * @return
     */
    String getTeamResult(UndefinedTeam.Type teamType);

    /**
     * The number of winning/losing rounds of the given team.
     *
     * @param isWin {@code true} if you want to get the number of winning rounds, {@code false} otherwise.
     * @param role  The team role.
     */
    int getScore(boolean isWin, TeamRole role);

    /**
     * Increase the number of winning rounds by 1.<br>
     * <b>This method should only be called during the Capture Mechanic time</b>
     *
     * @param teamType The undefined team type.<br>
     */
    void increaseWinPhase(UndefinedTeam.Type teamType);

    /**
     * Return the number of winning rounds of the given undefined team type.<br>
     * <b>This method should only be called during the Capture Mechanic time</b>
     *
     * @param teamType Undefined team type
     * @return Winning rounds
     */
    int getWinPhase(UndefinedTeam.Type teamType);

    /**
     * Record the round result for the given team.
     *
     * @param round The round
     * @param team  The team
     * @param isWin {@code true} if the team won, {@code false} otherwise.
     */
    void setScore(int round, TeamRole team, boolean isWin);

}
