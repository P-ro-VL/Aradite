package aradite.team;

import aradite.agent.Agent;
import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameData;
import aradite.data.packet.type.PlayerInGameLastDamagePacket;
import aradite.data.packet.type.PlayerInGameMVPPacket;
import aradite.match.Match;
import aradite.match.MatchScore;
import aradite.team.type.Attacker;
import aradite.team.type.Defender;
import aradite.team.type.UndefinedTeam;
import aradite.team.type.UndefinedTeam.UndefinedTeamType;
import com.destroystokyo.paper.Title;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.java.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A group of players in the game. There are two teams in the game.
 *
 * @author phongphong28
 */
public class MatchTeam {

    /**
     * Indicates the number of players that the arena requires to be started.
     */
    public static final int MAX_PLAYER_TO_START = 6;
    private final Match match;

    private Map<UUID, Agent> selectedAgents = Maps.newConcurrentMap();
    private Map<UUID, BossBar> bossBarMap = Maps.newHashMap();
    private Map<TeamRole, Team> teams = Maps.newConcurrentMap();

    public MatchTeam(Match match) {
        this.match = match;
    }

    /**
     * Reset the team data for new matches.
     */
    public void clear() {
        this.selectedAgents.clear();
        this.bossBarMap.clear();
        this.teams.clear();
    }

    /**
     * Define which team will become the attacker team after 3 first rounds.
     */
    public void defineAttacker(UndefinedTeam.UndefinedTeamType teamType) {
        UndefinedTeam undefinedTeam = (UndefinedTeam) teams.get(TeamRole.UNDEFINED);
        List<Player> winnings = undefinedTeam.getPlayerInTeam(teamType);
        List<Player> defeatings = undefinedTeam.getPlayerInTeam(UndefinedTeamType.not(teamType));

        Attacker attackerTeam = new Attacker();
        attackerTeam.addAll(winnings);

        Defender defenderTeam = new Defender();
        defenderTeam.addAll(defeatings);

        teams.put(TeamRole.ATTACK, attackerTeam);
        teams.put(TeamRole.DEFEND, defenderTeam);

        teams.remove(TeamRole.UNDEFINED);

        MatchScore matchScore = match.getMatchScore();
        String[] splittedTeamAResult = matchScore.getTeamResult(teamType).split("");
        for (int i = 1; i <= 3; i++) {
            boolean isWin = splittedTeamAResult[i-1].equalsIgnoreCase("1");
            matchScore.setScore(i, TeamRole.ATTACK, isWin);
        }
    }

    /**
     * Select an agent for the given player.
     *
     * @param player The player
     * @param agent  The agent he picked.
     */
    public void setSelectedAgents(Player player, Agent agent) {
        this.selectedAgents.put(player.getUniqueId(), agent);
    }

    /**
     * Return the representing color of the team that the given player in.
     *
     * @param player The player
     * @return The color of player's team
     */
    public String getTeamColor(Player player) {
        TeamRole teamRole = getPlayerTeam(player);
        if (teamRole == TeamRole.ATTACK) return "§c";
        if (teamRole == TeamRole.DEFEND) return "§b";

        UndefinedTeam undefinedTeam = (UndefinedTeam) getTeam(teamRole);
        return undefinedTeam.getTeamOf(player) == UndefinedTeamType.A ? "§b" : "§c";
    }

    /**
     * A {@link Map} contains agents picked by players in agent select phase.
     */
    public Map<UUID, Agent> getSelectedAgents() {
        return selectedAgents;
    }

    /**
     * Return a {@link List} containing all players of two teams and observers.
     */
    public List<Player> getPlayersAndObservers() {
        List<Player> players = Lists.newArrayList();
        teams.values().stream().map(team -> team.getMembers()).collect(Collectors.toList())
                .forEach(list -> list.forEach(player -> players.add(player)));
        return players;
    }

    /**
     * Return a {@link List} containing all players of two teams.
     */
    public List<Player> getAllPlayers() {
        List<Player> players = Lists.newArrayList();
        teams.values().stream().filter(team -> team.getRole() != TeamRole.OBSERVER).map(team -> team.getMembers())
                .collect(Collectors.toList()).forEach(list -> list.forEach(player -> players.add(player)));
        return players;
    }

    /**
     * Return a team that the given {@code player} is currently in.
     */
    public Team getTeamOf(Player player) {
        TeamRole type = getPlayerTeam(player);
        if (type == null)
            return null;
        return getTeam(type);
    }

    public Team getTeam(TeamRole role) {
        return teams.get(role);
    }

    /**
     * Return the type of team that the given player is in.
     *
     * @param player Player to check
     * @return Player's team type
     */
    public TeamRole getPlayerTeam(Player player) {
        for (Team team : teams.values()) {
            if (team.getMembers().contains(player))
                return team.getRole();
        }
        return null;
    }

    /**
     * Check if these two players are on the same team.
     *
     * @param player   Player A
     * @param comparer Player B
     * @return {@code true} if they are on the same team, {@code false} otherwise.
     */
    public boolean isOnSameTeam(Player player, Player comparer) {
        Team teamA = getTeamOf(player);
        Team teamB = getTeamOf(comparer);
        if (teamA.getRole() == teamB.getRole() && teamA.getRole() == TeamRole.UNDEFINED) {
            UndefinedTeam team = (UndefinedTeam) teamA;
            UndefinedTeamType teamAType = team.getTeamOf(player);
            UndefinedTeamType teamBType = team.getTeamOf(comparer);
            return teamAType == teamBType;
        } else {
            if (teamA.getRole() == teamB.getRole())
                return true;
        }
        return false;
    }

    /**
     * Define a team. This method can only be called when a game is started.
     *
     * @param role The role of the team.
     * @param team The team
     */
    public void defineTeam(TeamRole role, Team team) {
        this.teams.put(role, team);
    }

    /**
     * Return the boss bar of the given player.
     *
     * @param player The player
     */
    public BossBar getBossBar(Player player) {
        if (!this.bossBarMap.containsKey(player.getUniqueId())) {
            BossBar bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_6, null);
            this.bossBarMap.put(player.getUniqueId(), bar);
        }
        return this.bossBarMap.get(player.getUniqueId());
    }

    /**
     * Update the title, color and style of all players' boss bars.<br>
     * Apart from the title, the others are nullable (no changes will be made).
     * If the {@code progress} is not in [0,1], the bar's progress will not be affected.
     *
     * @param newTitle New title
     * @param color    New color
     * @param style    New style
     * @param progress New progress
     */
    public void updateAllBossbars(String newTitle, BarColor color, BarStyle style, double progress) {
        bossBarMap.values().forEach(bar -> {
            bar.setTitle(newTitle);
            if (color != null) bar.setColor(color);
            if (style != null) bar.setStyle(style);
            if (progress >= 0 && progress <= 1) bar.setProgress(progress);
        });
    }

    /**
     * Broadcast a message to all players in the match.
     *
     * @param broadcastType Type of broadcast
     * @param message       The message
     */
    public void broadcast(BroadcastType broadcastType, String message) {
        for (Player player : getAllPlayers()) {
            switch (broadcastType) {
                case NORMAL_CHAT:
                    player.sendMessage(message);
                    break;
                case CENTER_CHAT:
                    player.sendMessage(StringUtils.alignCenter(ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, message));
                    break;
                case ACTION_BAR:
                    player.sendActionBar(message);
                    break;
                case TITLE:
                    player.sendTitle(message, "");
                    break;
                case SUBTITLE:
                    player.sendTitle("", message);
                    break;
            }
        }
    }

    /**
     * Send the title to all players in the match.
     *
     * @param title The title
     */
    public void broadcastTitle(Title title) {
        getAllPlayers().forEach(player -> player.sendTitle(title));
    }

    /**
     * Return the Player Of the Game. (Player who has the highest mvp point among all players).
     */
    public Map.Entry<Player, Double> getPOG() {
        return getMatchMVPs().entrySet().stream().findFirst().orElse(null);
    }

    /**
     * Return the MVP player of the given team.
     *
     * @param team The team
     * @return The MVP
     */
    public Map.Entry<Player, Double> getMVPOf(TeamRole team) {
        for (Map.Entry<Player, Double> entry : getMatchMVPs().entrySet()) { // Because we've sorted the map, we just
            // need to loop the map from top to bottom
            TeamRole playerTeam = getPlayerTeam(entry.getKey());
            if (team == playerTeam) return entry;
        }
        return null;
    }

    /**
     * Return a sorted map about all players' mvp points.
     */
    private Map<Player, Double> getMatchMVPs() {
        Map<Player, Double> sortedMVPMap = Maps.newLinkedHashMap();
        for (Player player : getAllPlayers()) {
            PlayerInGameData data = match.retrieveProtocol(player);
            PlayerInGameMVPPacket packet = (PlayerInGameMVPPacket) data.getPacket(PacketType.INGAME_MVP);
            sortedMVPMap.put(player, packet.getTotalMVPPoint());
        }

        sortedMVPMap = pdx.mantlecore.java.map.Maps.sortByValue(sortedMVPMap,
                pdx.mantlecore.java.map.Maps.SORT_LARGEST_TO_SMALLEST);
        return sortedMVPMap;

    }

    /**
     * Return {@code true} if player has died in the match, {@code false} otherwise.
     *
     * @param player The player
     */
    public boolean isDead(Player player) {
        PlayerInGameLastDamagePacket lastDamagePacket = (PlayerInGameLastDamagePacket) match.retrieveProtocol(player)
                .getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);
        return lastDamagePacket.isDead();
    }

    public static enum BroadcastType {
        NORMAL_CHAT,
        CENTER_CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE;
    }

}
