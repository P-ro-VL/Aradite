package com.github.tezvn.aradite.impl.match;

import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBar;
import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBarType;
import com.github.tezvn.aradite.api.data.DataController;
import com.github.tezvn.aradite.api.data.PlayerData;
import com.github.tezvn.aradite.api.data.Statistic;
import com.github.tezvn.aradite.api.data.log.Report;
import com.github.tezvn.aradite.api.data.log.ReportForm;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.*;
import com.github.tezvn.aradite.api.match.mechanic.Mechanic;
import com.github.tezvn.aradite.api.match.mechanic.MechanicType;
import com.github.tezvn.aradite.api.packet.PacketType;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameData;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameMVPPacket;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameMVPPacket.MVPStatistics;
import com.github.tezvn.aradite.api.task.MatchTask;
import com.github.tezvn.aradite.api.team.MatchTeam;
import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.UndefinedTeam;
import com.github.tezvn.aradite.api.world.MapType;
import com.github.tezvn.aradite.api.world.MatchMap;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.agent.attribute.statusbar.AgentHealthBarImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameDataImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameLastDamagePacketImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameMVPPacketImpl;
import com.github.tezvn.aradite.impl.match.mechanic.ingame.BombCartMechanicImpl;
import com.github.tezvn.aradite.impl.match.mechanic.ingame.CaptureMechanicImpl;
import com.github.tezvn.aradite.impl.task.MatchTaskImpl;
import com.github.tezvn.aradite.impl.task.type.AgentSelectTask;
import com.github.tezvn.aradite.impl.task.type.CountdownToStartTask;
import com.github.tezvn.aradite.impl.team.MatchTeamImpl;
import com.github.tezvn.aradite.impl.team.type.UndefinedTeamImpl;
import com.github.tezvn.aradite.impl.ui.endmatch.MatchSumUpUI;
import com.github.tezvn.aradite.impl.world.DefaultMatchMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import pdx.mantlecore.java.Pair;
import pdx.mantlecore.java.Splitter;
import pdx.mantlecore.java.collection.Lists;
import pdx.mantlecore.java.security.IDGenerator;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.task.TaskQueue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

/**
 * Each game is represented by which is called "match".<br>
 * All data about it also are managed here.
 *
 * @author phongphong28
 */
public class MatchImpl implements Match {

    public static final short PLAYER_TO_START = 2; //TODO 2 FOR TEST MODE

    private static final Language lang = AraditeImpl.getInstance().getLanguage();

    private final List<Player> waitingPlayers = Lists.newArrayList();

    private final Map<UUID, PlayerInGameData> data = Maps.newConcurrentMap();
    private final MatchTeam matchTeam;
    private final MatchTask matchTask;
    private final MatchMap matchMap;
    private final MatchScore matchScore;
    private final MatchType matchType;
    private final Map<MatchFlag, Boolean> matchFlags = Maps.newHashMap();
    private final Table<Player, StatusBarType, StatusBar> statusBars = HashBasedTable.create();
    private String uuid;
    private MatchPhase phase;
    private Report matchReport;
    private boolean isShuffled = false;
    private Mechanic currentMechanic;

    public MatchImpl(MatchType matchType, MapType mapType) {
        this.uuid = IDGenerator.of(7).generate();
        this.matchType = matchType;
        this.matchMap = new DefaultMatchMap(mapType);

        this.matchTask = new MatchTaskImpl();
        this.matchTeam = new MatchTeamImpl(this);
        this.phase = MatchPhase.WAITING;
        this.matchScore = new MatchScoreImpl(this);

        this.matchReport = new Report();
    }

    /**
     * Return the status bars table.
     */
    public Table<Player, StatusBarType, StatusBar> getStatusBars() {
        return statusBars;
    }

    /**
     * Change the map's uuid
     *
     * @param uuid New uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Return the score manager of the match.
     */
    public MatchScore getMatchScore() {
        return matchScore;
    }

    /**
     * Set up the flag value for the match.
     *
     * @param flag  The flag
     * @param value The value
     */
    public void setupFlag(MatchFlag flag, boolean value) {
        this.matchFlags.put(flag, value);
    }

    /**
     * Return the current value of the given {@code flag}.
     */
    public boolean getFlag(MatchFlag flag) {
        return matchFlags.get(flag);
    }

    /**
     * Return the currently running mechanic.
     */
    public Mechanic getCurrentMechanic() {
        return currentMechanic;
    }

    /**
     * Change the current mechanic.<br>
     * This method can getOpposite be separately run but through
     *
     * @param currentMechanic New current mechanic.
     */
    public void setCurrentMechanic(Mechanic currentMechanic) {
        this.currentMechanic = currentMechanic;
    }

    /**
     * Return the map of the match.
     */
    public MatchMap getMatchMap() {
        return matchMap;
    }

    /**
     * Return the type of the match.
     *
     * @see MatchType
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Return all players' ingame packets.
     */
    public List<PlayerInGameData> getProtocols() {
        return new ArrayList<>(this.data.values());
    }

    /**
     * Return the ingame packet of specific {@link Player} whose uuid is
     * {@code uuid}.
     */
    public PlayerInGameData retrieveProtocol(Player player) {
        return this.data.get(player.getUniqueId());
    }

    /**
     * Return the task manager of the match.
     */
    public MatchTask getMatchTask() {
        return matchTask;
    }

    /**
     * Return the report of the match.
     */
    public Report getReport() {
        return matchReport;
    }

    /**
     * Return the match's team controller.
     */
    public MatchTeam getMatchTeam() {
        return matchTeam;
    }

    /**
     * Return the {@code ID} of the game.<br>
     * The UUID will be automatically generated whenever a game starts.
     */
    public String getUniqueID() {
        return uuid;
    }

    /**
     * Return the current phase of the game.
     */
    public MatchPhase getPhase() {
        return phase;
    }

    /**
     * Change the current phase of the game.
     */
    public void setPhase(MatchPhase phase) {
        getReport().log("Match phase change from " + this.phase.toString() + " to " + phase.toString() + ".");
        this.phase = phase;
    }

    /**
     * Return all players who are waiting for the game to start.
     */
    public List<Player> getWaitingPlayers() {
        return waitingPlayers;
    }

    /**
     * Force a player to join the game.
     *
     * @param player Player to be forced
     * @return {@code true} if joining successfully, {@code false} otherwise.
     */
    public boolean join(Player player) {
        if (getWaitingPlayers().size() > PLAYER_TO_START || getPhase() != MatchPhase.WAITING)
            return false;
        getWaitingPlayers().add(player);

        getWaitingPlayers().forEach(p ->
                p.sendMessage(lang.getString("match.broadcast.join")
                        .replace("%player%", player.getName())
                        .replace("%current_size%", "" + getWaitingPlayers().size())
                        .replace("%player_to_start%", "" + PLAYER_TO_START))
        );

        if (getWaitingPlayers().size() == PLAYER_TO_START) {

            CountdownToStartTask countdownTask = new CountdownToStartTask(this);
            countdownTask.start();
        }
        return true;
    }

    /**
     * Force to start the match.
     */
    public void start() {
        this.matchReport = new Report();
        this.matchReport.recordLogTime(true);

        setupFlag(MatchFlag.ALL_PLAYERS_INVUNERABLE, true);

        getReport().log("[INITIALIZING] The match is started.");
        setupProtocol();
        getReport().log("[INITIALIZING] Set up all protocol data for players.");
        shuffle();
        getReport().log("[INITIALIZING] Shuffle and devide players in two teams (A and B) complete.");

        getReport().log("[AGENT_SELECT] Starting Agent Select phase.");
        setPhase(MatchPhase.AGENT_SELECT);
        AgentSelectTask task = new AgentSelectTask(this);
        task.start();
        System.out.println("chay den day roi");
    }

    /**
     * Setup status bars for players.
     */
    public void setupStatusBars() {
        DataController dataController = AraditeImpl.getInstance().getDataController();
        getMatchTeam().getAllPlayers().forEach(player -> {
            for (StatusBarType barType : StatusBarType.values()) {
                StatusBar bar = new AgentHealthBarImpl(player, this);
                bar.start();

                statusBars.put(player, barType, bar);
            }

            PlayerData playerDataStorage = dataController.getUserData(player.getUniqueId());
            playerDataStorage.increase(Statistic.TOTAL_MATCH, 1);
        });
    }

    /**
     * Set up data and ingame data packet for all waiting players.
     */
    public void setupProtocol() {
        DataController controller = AraditeImpl.getInstance().getDataController();
        this.getWaitingPlayers().forEach(player -> {
            PlayerInGameData data = new PlayerInGameDataImpl();

            this.data.put(player.getUniqueId(), data);
            PlayerData storage = controller.getUserData(player.getUniqueId());
            storage.increase(Statistic.TOTAL_MATCH, 1);

            PlayerInGameLastDamagePacket lastDamagePacket = new PlayerInGameLastDamagePacketImpl(player);
            PlayerInGameMVPPacket mvpPacket = new PlayerInGameMVPPacketImpl(player);
            data.registerPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE, lastDamagePacket);
            data.registerPacket(PacketType.INGAME_MVP, mvpPacket);
        });
    }

    /**
     * Categorize players who are waiting in the match into two different undefined
     * teams.
     */
    public void shuffle() {
//        if (isShuffled)
//            return;
        Collections.shuffle(getWaitingPlayers());
        Queue<Player> queue = new LinkedList<>();
        queue.addAll(getWaitingPlayers());

        UndefinedTeam team = new UndefinedTeamImpl();

        boolean isATeam = true;
        for (Player player : getWaitingPlayers()) {
            team.addPlayer(player, isATeam ? UndefinedTeam.Type.A : UndefinedTeam.Type.B);
            isATeam = !isATeam;
        }
//        isShuffled = true;

        getMatchTeam().defineTeam(TeamRole.UNDEFINED, team);
    }

    /**
     * Start a game mechanic.<bR>
     * This method is usually called to start the actual game or change mechanic when the last one has finished.
     *
     * @param mechanicType Type of mechanic to start
     */
    public void runMechanic(MechanicType mechanicType, int index) {
        switch (mechanicType) {
            case CAPTURE:
                this.currentMechanic = new CaptureMechanicImpl(this);
                break;
            case BOMB_CART:
                this.currentMechanic = new BombCartMechanicImpl(this);
                break;
        }

        this.currentMechanic.setIndex(index);
        this.currentMechanic.start();

        getMatchTask().runMechanicManagement(this, currentMechanic);
    }

    /**
     * Finish the match.
     */
    public void finish() {
        getReport().log("[FINISH] Finishing the match ...");

        this.statusBars.values().forEach(StatusBar::kill);

        /*
         * Set up medals
         */
        ((MatchScoreImpl) getMatchScore()).initMedal();

        /*
         Investigate scores.
         */
        int atkWinRounds = getMatchScore().getScore(true, TeamRole.ATTACK);
        int defWinRounds = getMatchScore().getScore(true, TeamRole.DEFEND);

        DataController dataController = AraditeImpl.getInstance().getDataController();
        TeamRole winningTeam = atkWinRounds > defWinRounds ? TeamRole.ATTACK : TeamRole.DEFEND;
        getMatchTeam().getTeam(winningTeam).getMembers().forEach(player -> {
            dataController.getUserData(player.getUniqueId()).increase(Statistic.VICTORY_MATCH, 1);
        });

        getMatchTeam().getAllPlayers().forEach(player -> {
            TaskQueue.runSync(AraditeImpl.getInstance(), () -> {
                player.setGameMode(GameMode.ADVENTURE);
            });

            PlayerData dataStorage = dataController.getUserData(player.getUniqueId());

            PlayerInGameMVPPacketImpl mvpPacket = retrieveProtocol(player).getPacket(PlayerInGameMVPPacketImpl.class);

            for (Statistic stats : Statistic.values())
                dataStorage.increase(stats, mvpPacket.getStatistic(stats));

            player.getInventory().clear();
        });

        /*
         * Investigate the MVPs and POG.
         */
        Entry<Player, Double> mvpAttacker = getMatchTeam().getMVPOf(TeamRole.ATTACK);
        Entry<Player, Double> mvpDefender = getMatchTeam().getMVPOf(TeamRole.DEFEND);
        Entry<Player, Double> pog = getMatchTeam().getPOG();

            /*
            Send summing up message.
            */
        Language lang = AraditeImpl.getInstance().getLanguage();
        List<String> sumUpBroadcast = lang.getList("match.finish.sum-up-broadcast");

        getReport().log("[FINISH] Broadcasting match result to all players ...");

        for (Player player : getMatchTeam().getAllPlayers()) {
            Menu.open(player, new MatchSumUpUI(this, player));
        }

        getReport().log("[FINISH] MATCH COMPLETED !");
        getReport().recordLogTime(false);

        ReportForm form = new ReportForm();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Report.REPORT_TIME_FORMAT);
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("MATCH-INFO\n");

        headerBuilder.append("Match ID : " + getUniqueID() + "\n");
        headerBuilder.append("Start time : " + getReport().getStartLogTime() + "\n\n");

        headerBuilder.append("PLAYERS\n");

        String[] attackerNames = getMatchTeam().getTeam(TeamRole.ATTACK).getMembers().stream()
                .map(HumanEntity::getName).toArray(String[]::new);
        String[] defenderNames = getMatchTeam().getTeam(TeamRole.DEFEND).getMembers().stream().map(Player::getName)
                .toArray(String[]::new);

        headerBuilder.append("Attacker Team : " + Splitter.newInstance().splitBy(", ")
                .appendElements(attackerNames).toString() + "\n");
        headerBuilder.append("Defender Team : " + Splitter.newInstance().splitBy(", ")
                .appendElements(defenderNames).toString() + "\n\n");

        headerBuilder.append("RESULT\n");
        headerBuilder.append("Score: Attacker " + atkWinRounds + " - " + defWinRounds + " Defender\n");
        headerBuilder.append("Attacker MVP/Defender MVP : \n"
                + mvpAttacker.getKey().getName() + "/" + mvpDefender.getKey().getName() + "\n");
        headerBuilder.append("POG : " + pog.getKey().getName() + "\n\n");
        headerBuilder.append("MVP POINT AND MEDALS\n");

        for (Player player : getMatchScore().getMedals().keySet()) {
            Splitter splitter = Splitter.newInstance();
            splitter.splitBy(", ");
            for (Pair<MVPStatistics, MedalRank> data : getMatchScore().getMedals().get(player)) {
                splitter.appendElements(data.getKey().toString() + "-" +
                        data.getValue().toString().toLowerCase());
            }
            splitter.appendElements("\n\t\tMVP points : ");

            PlayerInGameMVPPacketImpl packet = (PlayerInGameMVPPacketImpl) retrieveProtocol(player)
                    .getPacket(PacketType.INGAME_MVP);
            for (MVPStatistics stats : MVPStatistics.values())
                splitter.appendElements(stats.toString() + "-" + packet.getMVPPoint(stats));
            headerBuilder.append(splitter + "\n");
        }

        form.addHeader(headerBuilder.toString());

        form.addFooter("-----------------------------------\n" +
                "The report was written at " + getReport().getEndLogTime());
        try {
            getReport().write(form);
        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("!!! CANNOT CREATE REPORT FILE FOR MATCH ID " + getUniqueID() + " !!!");
        }

        this.data.clear();
        this.phase = MatchPhase.WAITING;

        this.isShuffled = false;
        this.currentMechanic = null;
        this.matchFlags.clear();

        getMatchTeam().clear();
        getMatchScore().clear();

        System.gc();
    }

}
