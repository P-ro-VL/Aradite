package com.github.tezvn.aradite.impl.match;

import com.github.tezvn.aradite.api.match.MatchScore;
import com.github.tezvn.aradite.api.match.MedalRank;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameMVPPacket.MVPStatistics;
import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.UndefinedTeam;
import com.github.tezvn.aradite.api.packet.PacketType;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameMVPPacketImpl;
import com.github.tezvn.aradite.impl.team.MatchTeamImpl;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import pdx.mantlecore.java.Pair;

import java.util.Map;

/**
 * Record the score of teams in the match.
 */
public class MatchScoreImpl implements MatchScore {

    private final MatchImpl match;
    private Table<Integer, TeamRole, Boolean> scoreTable = HashBasedTable.create();
    private Multimap<Player, Pair<MVPStatistics, MedalRank>> medals = HashMultimap.create();

    private String teamAResult = "", teamBResult = "";

    public MatchScoreImpl(MatchImpl match) {
        this.match = match;
    }

    /**
     * Clear the data for new matches.
     */
    public void clear() {
        this.scoreTable.clear();
        this.medals.clear();
    }

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
    public Table<Integer, TeamRole, Boolean> getScoreTable() {
        return scoreTable;
    }

    /**
     * Return the initialized medal map.
     */
    public Multimap<Player, Pair<MVPStatistics, MedalRank>> getMedals() {
        return medals;
    }

    /**
     * Return the result of the given undefined team
     * @param teamType
     * @return
     */
    public String getTeamResult(UndefinedTeam.Type teamType) {
        return teamType == UndefinedTeam.Type.A ? teamAResult : teamBResult;
    }

    /**
     * The number of winning/losing rounds of the given team.
     *
     * @param isWin {@code true} if you want to get the number of winning rounds, {@code false} otherwise.
     * @param role  The team role.
     */
    public int getScore(boolean isWin, TeamRole role) {
        int amount = 0;
        for (Map<TeamRole, Boolean> map : scoreTable.rowMap().values()) {
            if (map.get(role) == isWin) amount++;
        }
        return amount;
    }

    /**
     * Increase the number of winning rounds by 1.<br>
     * <b>This method should only be called during the Capture Mechanic time</b>
     *
     * @param teamType The undefined team type.<br>
     */
    public void increaseWinPhase(UndefinedTeam.Type teamType) {
        switch (teamType) {
            case A:
                this.teamAResult = teamAResult + "1";
                this.teamBResult = teamBResult + "0";
                break;
            default:
                this.teamBResult = teamBResult + "1";
                this.teamAResult = teamAResult = "0";
                break;
        }
    }

    /**
     * Return the number of winning rounds of the given undefined team type.<br>
     * <b>This method should only be called during the Capture Mechanic time</b>
     *
     * @param teamType Undefined team type
     * @return Winning rounds
     */
    public int getWinPhase(UndefinedTeam.Type teamType) {
        switch (teamType) {
            case A:
                return defineWin(teamAResult);
            default:
                return defineWin(teamBResult);
        }
    }

    private int defineWin(String result) {
        String[] splitted = result.split("");
        int amount = 0;
        for (String s : splitted) if (s.equalsIgnoreCase("1")) amount++;
        return amount;
    }

    /**
     * Record the round result for the given team.
     *
     * @param round The round
     * @param team  The team
     * @param isWin {@code true} if the team won, {@code false} otherwise.
     */
    public void setScore(int round, TeamRole team, boolean isWin) {
        this.scoreTable.put(round, team, isWin);
        this.scoreTable.put(round, team.getOpposite(), !isWin);
        match.getReport().log("[MATCH-SCORE] Recorded score : Round " + round + " / Team " + team.toString() + " / Win " + isWin);
    }

    /**
     * Initialize and calculate the medals basing on the mvp score for all players.<br>
     * This method must be called before any other processes when finishing the match.
     */
    public void initMedal() {
        match.getReport().log("[FINISH] Initialize medals for players ...");
        MatchTeamImpl team = match.getMatchTeam();
        outer:
        for (Player player : team.getAllPlayers()) {
            PlayerInGameMVPPacketImpl mvpPacket = (PlayerInGameMVPPacketImpl) match.retrieveProtocol(player)
                    .getPacket(PacketType.INGAME_MVP);
            inner:
            for (MVPStatistics stats : MVPStatistics.values()) {
                double point = mvpPacket.getMVPPoint(stats);
                MedalRank rank = stats.getRankWithGivenPoint(point);
                this.medals.put(player, Pair.of(stats, rank));
            }
        }
    }
}
