package com.github.tezvn.aradite.match;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.attribute.statusbar.StatusBar;
import com.github.tezvn.aradite.agent.attribute.statusbar.StatusBarType;
import com.github.tezvn.aradite.data.DataController;
import com.github.tezvn.aradite.data.Statistic;
import com.github.tezvn.aradite.data.global.PlayerDataStorage;
import com.github.tezvn.aradite.data.log.Report;
import com.github.tezvn.aradite.data.log.ReportForm;
import com.github.tezvn.aradite.data.packet.PacketType;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameData;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameMVPPacket;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.mechanic.Mechanic;
import com.github.tezvn.aradite.match.mechanic.MechanicType;
import com.github.tezvn.aradite.match.mechanic.ingame.BombCartMechanic;
import com.github.tezvn.aradite.match.mechanic.ingame.CaptureMechanic;
import com.github.tezvn.aradite.task.MatchTask;
import com.github.tezvn.aradite.task.type.AgentSelectTask;
import com.github.tezvn.aradite.task.type.CountdownToStartTask;
import com.github.tezvn.aradite.team.MatchTeam;
import com.github.tezvn.aradite.team.TeamRole;
import com.github.tezvn.aradite.task.type.MechanicManagementTask;
import com.github.tezvn.aradite.team.type.UndefinedTeam;
import com.github.tezvn.aradite.team.type.UndefinedTeam.UndefinedTeamType;
import com.github.tezvn.aradite.ui.endmatch.MatchSumUpUI;
import com.github.tezvn.aradite.world.MapType;
import com.github.tezvn.aradite.world.MatchMap;
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
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.tezvn.aradite.data.packet.type.PlayerInGameMVPPacket.MVPStatistics;
import static java.util.Map.Entry;

/**
 * Each game is represented by which is called "match".<br>
 * All data about it also are managed here.
 *
 * @author phongphong28
 */
public class Match {

    public static final short PLAYER_TO_START = 2; //TODO 2 FOR TEST MODE

    private static Language lang = Aradite.getInstance().getLanguage();

    private final List<Player> waitingPlayers = Lists.newArrayList();

    private Map<UUID, PlayerInGameData> data = Maps.newConcurrentMap();

    private String uuid;

    private MatchPhase phase;

    private MatchTeam matchTeam;
    private MatchTask matchTask;
    private MatchMap matchMap;
    private MatchScore matchScore;

    private Report matchReport;

    private MatchType matchType;

    private boolean isShuffled = false;
    private Mechanic currentMechanic;
    private Map<MatchFlag, Boolean> matchFlags = Maps.newHashMap();

    private Table<Player, StatusBarType, StatusBar> statusBars = HashBasedTable.create();

    public Match(MatchType matchType, MapType mapType) {
        this.uuid = IDGenerator.of(7).generate();
        this.matchType = matchType;
        this.matchMap = new MatchMap(mapType);

        this.matchTask = new MatchTask();
        this.matchTeam = new MatchTeam(this);
        this.phase = MatchPhase.WAITING;
        this.matchScore = new MatchScore(this);

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
     * Change the current mechanic.<br>
     * This method can not be separately run but through
     * {@link MechanicManagementTask MechanicManagementTask}.
     *
     * @param currentMechanic New current mechanic.
     */
    public void setCurrentMechanic(Mechanic currentMechanic) {
        this.currentMechanic = currentMechanic;
    }

    /**
     * Return the currently running mechanic.
     */
    public Mechanic getCurrentMechanic() {
        return currentMechanic;
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
        return this.data.values().stream().collect(Collectors.toList());
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
        DataController dataController = Aradite.getInstance().getDataController();
        getMatchTeam().getAllPlayers().forEach(player -> {
            for (StatusBarType barType : StatusBarType.values()) {
                try {
                    Class<? extends StatusBar> wrapper = barType.getWrapper();
                    Constructor<? extends StatusBar> constructor = wrapper.getDeclaredConstructor(Player.class, Match.class);
                    StatusBar bar = constructor.newInstance(player, this);
                    bar.start();

                    statusBars.put(player, barType, bar);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }

            PlayerDataStorage playerDataStorage = dataController.getUserData(player.getUniqueId());
            playerDataStorage.increase(Statistic.TOTAL_MATCH, 1);
        });
    }

    /**
     * Set up data and ingame data packet for all waiting players.
     */
    public void setupProtocol() {
        DataController controller = Aradite.getInstance().getDataController();
        this.getWaitingPlayers().forEach(player -> {
            PlayerInGameData data = new PlayerInGameData();

            this.data.put(player.getUniqueId(), data);
            PlayerDataStorage storage = controller.getUserData(player.getUniqueId());
            storage.increase(Statistic.TOTAL_MATCH, 1);

            PlayerInGameLastDamagePacket lastDamagePacket = new PlayerInGameLastDamagePacket(player);
            PlayerInGameMVPPacket mvpPacket = new PlayerInGameMVPPacket(player);
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

        UndefinedTeam team = new UndefinedTeam();

        boolean isATeam = true;
        for (Player player : getWaitingPlayers()) {
            team.addPlayer(player, isATeam ? UndefinedTeamType.A : UndefinedTeamType.B);
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
                this.currentMechanic = new CaptureMechanic(this);
                break;
            case BOMB_CART:
                this.currentMechanic = new BombCartMechanic(this);
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
        getMatchScore().initMedal();

        /*
         Investigate scores.
         */
        int atkWinRounds = getMatchScore().getScore(true, TeamRole.ATTACK);
        int defWinRounds = getMatchScore().getScore(true, TeamRole.DEFEND);

        DataController dataController = Aradite.getInstance().getDataController();
        TeamRole winningTeam = atkWinRounds > defWinRounds ? TeamRole.ATTACK : TeamRole.DEFEND;
        getMatchTeam().getTeam(winningTeam).getMembers().forEach(player -> {
            dataController.getUserData(player.getUniqueId()).increase(Statistic.VICTORY_MATCH, 1);
        });

        getMatchTeam().getAllPlayers().forEach(player -> {
            TaskQueue.runSync(Aradite.getInstance(), () -> {
                player.setGameMode(GameMode.ADVENTURE);
            });

            PlayerDataStorage dataStorage = dataController.getUserData(player.getUniqueId());

            PlayerInGameMVPPacket mvpPacket = retrieveProtocol(player).getPacket(PlayerInGameMVPPacket.class);

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
        Language lang = Aradite.getInstance().getLanguage();
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

            PlayerInGameMVPPacket packet = (PlayerInGameMVPPacket) retrieveProtocol(player)
                    .getPacket(PacketType.INGAME_MVP);
            for (MVPStatistics stats : MVPStatistics.values())
                splitter.appendElements(stats.toString() + "-" + packet.getMVPPoint(stats));
            headerBuilder.append(splitter.toString() + "\n");
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
