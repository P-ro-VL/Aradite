package aradite.match.mechanic.ingame;

import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameMVPPacket;
import aradite.match.Match;
import aradite.match.MatchScore;
import aradite.match.mechanic.AbstractMechanic;
import aradite.match.mechanic.MechanicType;
import aradite.task.AraditeTask;
import aradite.task.AsyncTimerTask;
import aradite.team.MatchTeam;
import aradite.team.TeamRole;
import aradite.team.type.UndefinedTeam;
import aradite.world.MatchLocationType;
import aradite.world.MatchMap;
import com.destroystokyo.paper.Title;
import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.java.StringUtils;
import pdx.mantlecore.math.PrimaryMath;
import pdx.mantlecore.math.Shapes;
import pdx.mantlecore.task.TaskQueue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptureMechanic extends AbstractMechanic {

    /**
     * The radius of the capture point.
     */
    public static final int CAPTURE_POINT_RADIUS = 8, POINT_PER_CAPTURE = 10, POINT_TO_WIN = 100,
            TOTAL_PHASE_AMOUNT = 3;

    private CaptureMechanicTask mechanicTask;

    public CaptureMechanic(Match match) {
        super(match);
    }

    @Override
    public MechanicType getMechanicType() {
        return MechanicType.CAPTURE;
    }

    @Override
    public String getID() {
        return "capture-mechanic";
    }

    @Override
    public AraditeTask getTask() {
        return this.mechanicTask;
    }

    @Override
    public void onStart() {
        Match match = getMatch();
        MatchTeam team = match.getMatchTeam();
        MatchMap matchMap = match.getMatchMap();
        UndefinedTeam undefinedTeam = (UndefinedTeam) team.getTeam(TeamRole.UNDEFINED);
        team.getAllPlayers().forEach(player -> {
            UndefinedTeam.UndefinedTeamType teamType = undefinedTeam.getTeamOf(player);
            Location teleLocation = matchMap.getLocation(MatchLocationType.UNDEFINED_TEAM_BASE).get(teamType.ordinal());
            player.teleportAsync(teleLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.sendTitle(new Title(lang.getString("mechanic." + getID() + ".start-title.title"),
                    lang.getString("mechanic." + getID() + ".start-title.sub-title")
                            .replaceAll("%round%", "" + getIndex()), 20, 60, 20));
            player.playSound(player.getEyeLocation(), Sound.UI_TOAST_OUT, 2, 1);
        });

        this.mechanicTask = new CaptureMechanicTask(this);
    }

    @Override
    public void onFinish() {
        UndefinedTeam.UndefinedTeamType winningTeam = mechanicTask.getWinningTeam();
        LinkedHashMap<Player, Double> mvps = mechanicTask.getMVPs();
        LinkedList<Map.Entry<Player, Double>> mvpEntries = new LinkedList<Map.Entry<Player, Double>>(mvps.entrySet());

        Match match = getMatch();
        MatchTeam team = match.getMatchTeam();
        UndefinedTeam undefinedTeam = (UndefinedTeam) team.getTeam(TeamRole.UNDEFINED);

        team.getAllPlayers().forEach(player -> {
            UndefinedTeam.UndefinedTeamType teamType = undefinedTeam.getTeamOf(player);
            boolean isWin = teamType == winningTeam;
            String path = isWin ? "winning-team" : "losing-team";

            String title = lang.getString("mechanic." + getID() + ".finish." + path + ".title");
            String subtitle = lang.getString("mechanic." + getID() + ".finish." + path + ".sub-title");

            player.sendTitle(new Title(title, subtitle, 20, 60, 20));
            player.playSound(player.getEyeLocation(),
                    isWin ? Sound.UI_TOAST_CHALLENGE_COMPLETE : Sound.ENTITY_BLAZE_DEATH,
                    2, 1);

            List<String> mvpBroadcast = lang.getList("mechanic." + getID() + ".finish.mvp-broadcast");
            int index = -1;
            for (String string : mvpBroadcast) {
                index++;
                String toSend = string.replaceAll("%phase_index%", "" + getIndex());

                if (index >= 0 && index < mvpEntries.size()) {
                    Map.Entry<Player, Double> entry = mvpEntries.get(index);
                    Player mvper = entry.getKey();
                    double mvpScore = entry.getValue();

                    UndefinedTeam.UndefinedTeamType mvpTeamType = undefinedTeam.getTeamOf(mvper);
                    String color = mvpTeamType == UndefinedTeam.UndefinedTeamType.A ? "§b" : "§c";

                    toSend = toSend.replaceAll("%team_color%", color).replaceAll("%player_name%", mvper.getName())
                            .replaceAll("%score%", "" + mvpScore);
                }

                player.sendMessage(StringUtils.alignCenter(ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, toSend));
            }

            MatchScore scoreData = match.getMatchScore();
            String matchScore = lang.getString("mechanic." + getID() + ".finish.score")
                    .replaceAll("%team_a_winning_phase%", "" + scoreData.getWinPhase(UndefinedTeam.UndefinedTeamType.A))
                    .replaceAll("%team_b_winning_phase%", "" + scoreData.getWinPhase(UndefinedTeam.UndefinedTeamType.B));
            player.sendMessage(StringUtils.alignCenter(ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, matchScore));
            player.sendMessage("");
            player.sendMessage(lang.getString("match.ingame.prepare-change-phase"));
        });

    }

    private class CaptureMechanicTask extends AsyncTimerTask {

        private final CaptureMechanic mechanic;
        private BossBar captureScoreBossBar;
        private String bossBarTitlePattern = "%team_a_name%          §b%a_score% §f§l|%capturing_team_color%⦿§f§l| §c%b_score%          %team_b_name%";

        private Location capturePoint;
        private String mechanicID;
        private UndefinedTeam undefinedTeam;
        private int clock = 0;
        private Map<UndefinedTeam.UndefinedTeamType, AtomicInteger> captureScore = Maps.newHashMap();
        private Map<Player, Double> mvpMap = Maps.newHashMap();

        /*
         * WINNING DATA
         */
        private UndefinedTeam.UndefinedTeamType winningTeam;

        public CaptureMechanicTask(CaptureMechanic mechanic) {
            super(TimeUnit.SECONDS, 1, mechanic.getID() + "-" + mechanic.getIndex());
            this.mechanicID = mechanic.getID();
            this.mechanic = mechanic;
        }

        @Override
        public void onExecute() {
            Match match = getMatch();
            MatchTeam team = match.getMatchTeam();
            MatchMap map = match.getMatchMap();
            capturePoint = map.getLocation(MatchLocationType.CAPTURE_POINT).get(getIndex() - 1);

            match.getReport().log("[" + mechanicID + "-" + getIndex() + "] The #" + getIndex() + " capture point " +
                    "was set up at location : " + capturePoint.getWorld().getName() + "/" + capturePoint.getBlockX() + "/"
                    + capturePoint.getBlockY() + "/" + capturePoint.getBlockZ());

            this.captureScoreBossBar = Bukkit.createBossBar(this.bossBarTitlePattern
                            .replaceAll("%team_a_name%", lang.getString("team.undefined.a"))
                            .replaceAll("%team_b_name%", lang.getString("team.undefined.b"))
                            .replaceAll("%a_score%", "" + 0)
                            .replaceAll("%b_score%", "" + 0)
                            .replaceAll("%capturing_team_color%", "§7"),
                    BarColor.BLUE, BarStyle.SEGMENTED_10, BarFlag.PLAY_BOSS_MUSIC);

            undefinedTeam = (UndefinedTeam) team.getTeam(TeamRole.UNDEFINED);
            team.getAllPlayers().forEach(player -> {
                player.sendMessage("");
                player.sendMessage(lang.getString("mechanic." + mechanicID + ".capture-point-set"));
                player.sendMessage("");

                this.mvpMap.put(player, 0.0);

                this.captureScoreBossBar.addPlayer(player);
            });

            this.captureScore.put(UndefinedTeam.UndefinedTeamType.A, new AtomicInteger(0));
            this.captureScore.put(UndefinedTeam.UndefinedTeamType.B, new AtomicInteger(0));
        }

        @Override
        public void run() {
            /*
             * Display the circle particle around the capture point.
             */
            Shapes.circle(this.capturePoint, CAPTURE_POINT_RADIUS, 50).forEach(point -> {
                point.getWorld().spawnParticle(Particle.REDSTONE, point.clone().add(0, 0.5, 0), 1,
                        new Particle.DustOptions(Color.ORANGE, 5));
            });

            /*
             * Check players
             */
            List<Player> surroundPlayers = new ArrayList<>(
                    this.capturePoint.getNearbyPlayers(CAPTURE_POINT_RADIUS, CAPTURE_POINT_RADIUS, CAPTURE_POINT_RADIUS));
            if (surroundPlayers.isEmpty()) {
                this.captureScoreBossBar.setTitle(this.bossBarTitlePattern
                        .replaceAll("%team_a_name%", lang.getString("team.undefined.a"))
                        .replaceAll("%team_b_name%", lang.getString("team.undefined.b"))
                        .replaceAll("%a_score%", "" + this.captureScore.get(UndefinedTeam.UndefinedTeamType.A).get())
                        .replaceAll("%b_score%", "" + this.captureScore.get(UndefinedTeam.UndefinedTeamType.B).get())
                        .replaceAll("%capturing_team_color%", "§7"));
                this.captureScoreBossBar.setColor(BarColor.WHITE);
                this.captureScoreBossBar.setProgress(1);
                this.clock = 0;
                return;
            }
            Player mocker = surroundPlayers.get(0);
            UndefinedTeam.UndefinedTeamType teamType = undefinedTeam.getTeamOf(mocker);
            Optional<Player> mockResult = surroundPlayers.stream()
                    .filter(player -> undefinedTeam.getTeamOf(player) != teamType).findAny();
            if (mockResult.isPresent()) {
                this.clock = 0;
                return; // Having a player of the other team in the capture point at the same time.
            }

            if (clock <= 3) { // Players must wait at least 5 seconds for the point to start capturing.
                clock++;
                return;
            }

            /*
             * Add up score.
             */
            AtomicInteger scoreAtomic = this.captureScore.get(teamType);
            int score = scoreAtomic.get();
            score += POINT_PER_CAPTURE * surroundPlayers.size();
            scoreAtomic.set(score);

            surroundPlayers.forEach(player -> {
                double mvpPoint = this.mvpMap.get(player);
                mvpPoint += POINT_PER_CAPTURE - surroundPlayers.size();
                this.mvpMap.put(player, mvpPoint);

                PlayerInGameMVPPacket mvpPacket = (PlayerInGameMVPPacket) getMatch().retrieveProtocol(player)
                        .getPacket(PacketType.INGAME_MVP);
                mvpPacket.addMVPPoint(PlayerInGameMVPPacket.MVPStatistics.CAPTURE_POINT, 5);
            });

            /*
             * Update boss bars' title.
             */
            this.captureScoreBossBar.setTitle(this.bossBarTitlePattern
                    .replaceAll("%team_a_name%", lang.getString("team.undefined.a"))
                    .replaceAll("%team_b_name%", lang.getString("team.undefined.b"))
                    .replaceAll("%a_score%", "" + this.captureScore.get(UndefinedTeam.UndefinedTeamType.A).get())
                    .replaceAll("%b_score%", "" + this.captureScore.get(UndefinedTeam.UndefinedTeamType.B).get())
                    .replaceAll("%capturing_team_color%", teamType == UndefinedTeam.UndefinedTeamType.A ? "§b" : "§c"));
            this.captureScoreBossBar.setColor(teamType == UndefinedTeam.UndefinedTeamType.A ? BarColor.BLUE : BarColor.RED);

            double progressPercent = PrimaryMath.percentage(Math.min(100, this.captureScore.get(teamType).get()), 100,
                    PrimaryMath.PercentageMode.ONE_DIGIT) / 100;
            this.captureScoreBossBar.setProgress(progressPercent);

            /*
             * Check winning
             */
            if (score >= POINT_TO_WIN) {
                MatchScore scoreData = getMatch().getMatchScore();
                this.winningTeam = teamType;
                if (teamType == UndefinedTeam.UndefinedTeamType.A)
                    scoreData.increaseWinPhase(UndefinedTeam.UndefinedTeamType.A);
                else
                    scoreData.increaseWinPhase(UndefinedTeam.UndefinedTeamType.B);
                mechanic.finish();

                this.captureScoreBossBar.removeAll();
            }


        }

        /**
         * Return the mechanic's winning team.<Br>
         * <b>This method can only be accessed once the mechanic is finished.</b>
         */
        public UndefinedTeam.UndefinedTeamType getWinningTeam() {
            return winningTeam;
        }

        /**
         * Return the top 3 MVP of the mechanic.<br>
         * <b>This method can only be accessed once the mechanic is finished.</b>
         */
        public LinkedHashMap<Player, Double> getMVPs() {
            Map<Player, Double> sortMap = Maps.newHashMap();
            sortMap = pdx.mantlecore.java.map.Maps.sortByValue(mvpMap, pdx.mantlecore.java.map.Maps.SORT_LARGEST_TO_SMALLEST);

            LinkedHashMap<Player, Double> mvpMap = Maps.newLinkedHashMap();
            int index = 0;
            for (Map.Entry<Player, Double> entry : sortMap.entrySet()) {
                if (index >= 3) break;
                mvpMap.put(entry.getKey(), entry.getValue());
                index++;
            }

            return mvpMap;
        }
    }
}
