package com.github.tezvn.aradite.match.mechanic.ingame;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.data.packet.PacketType;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameMVPPacket;
import com.github.tezvn.aradite.match.Match;
import com.github.tezvn.aradite.match.MatchScore;
import com.github.tezvn.aradite.match.mechanic.AbstractMechanic;
import com.github.tezvn.aradite.match.mechanic.MechanicType;
import com.github.tezvn.aradite.task.AraditeTask;
import com.github.tezvn.aradite.task.AsyncTimerTask;
import com.github.tezvn.aradite.team.MatchTeam;
import com.github.tezvn.aradite.team.TeamRole;
import com.github.tezvn.aradite.team.type.Attacker;
import com.github.tezvn.aradite.team.type.Defender;
import com.github.tezvn.aradite.world.MatchLocationType;
import com.github.tezvn.aradite.world.MatchMap;
import com.destroystokyo.paper.Title;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.item.ItemCheckFactory;
import pdx.mantlecore.java.StringUtils;
import pdx.mantlecore.math.PrimaryMath;
import pdx.mantlecore.task.TaskQueue;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BombCartMechanic extends AbstractMechanic {

    public static final int PUSH_MINECART_RANGE = 5, TOTAL_TIME_TO_PUSH = 180, DEFUSING_TIME = 8,
            TOTAL_PHASE_AMOUNT = 3;

    public static final ItemStack DEFUSING_ITEM = new ItemBuilder(Material.SHEARS)
            .setDisplayName(lang.getString("mechanic.bomb-cart.defusing-item.displayname"))
            .setLore(lang.getList("mechanic.bomb-cart.defusing-item.lore").stream()
                    .map(string -> string.replace("%defusing_time%", "" + DEFUSING_TIME))
                    .collect(Collectors.toList()))
            .create();

    private Location cartStart;
    private Location cartEnd;
    private BossBar bombScoreBar;
    private String bombScoreBarPattern = "§c§l%attacker_score%          %countdown_time%          §b§l%defender_score%";
    private BombCartMechanicTask task;
    private Stack<WinningReason> winningReasons = new Stack<>();
    private BossBar bombDefusingBar;
    private String bombDefusingBarPattern;

    public BombCartMechanic(Match match) {
        super(match);
    }

    @Override
    public MechanicType getMechanicType() {
        return MechanicType.BOMB_CART;
    }

    @Override
    public String getID() {
        return "bomb-cart";
    }

    @Override
    public AraditeTask getTask() {
        return task;
    }

    @Override
    public void onStart() {
        Match match = getMatch();
        MatchTeam team = match.getMatchTeam();
        MatchMap matchMap = match.getMatchMap();

        Attacker attackerTeam = (Attacker) team.getTeam(TeamRole.ATTACK);
        Defender defenderTeam = (Defender) team.getTeam(TeamRole.DEFEND);

        team.getAllPlayers().forEach(player -> {
            TeamRole teamRole = team.getPlayerTeam(player);
            String teamRoleString = teamRole.toString();
            Location teleLocation = matchMap.getLocation(
                            MatchLocationType.valueOf("TEAM_" + teamRole.toString() + "_BASE"))
                    .get(0);
            player.teleportAsync(teleLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.sendTitle(new Title(lang.getString("mechanic." + getID() + ".start-title."
                    + teamRoleString.toLowerCase() + ".title"),
                    lang.getString("mechanic." + getID() + ".start-title." + teamRoleString.toLowerCase() + ".sub-title")
                            .replaceAll("%round%", "" + getIndex()), 20, 60, 20));
            player.playSound(player.getEyeLocation(), Sound.UI_TOAST_OUT, 2, 1);
        });

        cartStart = matchMap.getLocation(MatchLocationType.BOMB_CART_START).get(0);
        cartEnd = matchMap.getLocation(MatchLocationType.BOMB_CART_END).get(0);

        task = new BombCartMechanicTask(this);
    }

    @Override
    public void onFinish() {
        MatchScore matchScore = getMatch().getMatchScore();

        StringBuilder scorePatternBuilder = new StringBuilder();
        for (int i = 0; i < TOTAL_PHASE_AMOUNT; i++) {
            if (i >= this.winningReasons.size())
                scorePatternBuilder.append("§7⦿");
            else
                scorePatternBuilder.append(winningReasons.get(i).getIcon());
        }
        String scorePattern = scorePatternBuilder.toString();
        this.bombScoreBar.setTitle(
                this.bombScoreBarPattern.replace("%attacker_score%", "" + matchScore.getScore(true, TeamRole.ATTACK))
                        .replace("%defender_score%", "" + matchScore.getScore(true, TeamRole.DEFEND))
                        .replaceAll("%countdown%", "00:00")
        );

        WinningReason lastWinReason = this.winningReasons.lastElement();
        TeamRole winningTeam = task.winningTeam;

        Match match = getMatch();
        MatchTeam team = match.getMatchTeam();
        MatchMap matchMap = match.getMatchMap();

        team.getAllPlayers().forEach(player -> {
            TeamRole teamRole = team.getPlayerTeam(player);
            String teamRoleString = teamRole.toString();
            String path = (teamRole == winningTeam ? "winning-team." : "losing-team.") + teamRole.toString().toLowerCase();

            player.sendTitle(new Title(lang.getString("mechanic." + getID() + ".finish." + path + ".title"),
                    lang.getString("mechanic." + getID() + ".finish." + path + ".sub-title")
                            .replaceAll("%round%", "" + getIndex()), 20, 60, 20));
            player.playSound(player.getEyeLocation(), Sound.UI_TOAST_OUT, 2, 1);

            List<String> resultBroadcast = lang.getList("mechanic." + getID() + ".finish.result-broadcast");
            resultBroadcast = resultBroadcast.stream().map(string ->
                    string.replace("%round%", "" + getIndex())
                            .replace("%result_description%",
                                    lang.getString("mechanic." + getID() + ".finish.result-description."
                                            + lastWinReason.toString()))
                            .replace("%attacker_score%", "" + matchScore.getScore(true, TeamRole.ATTACK))
                            .replace("%defender_score%", "" + matchScore.getScore(true, TeamRole.DEFEND))
            ).collect(Collectors.toList());
            resultBroadcast.forEach(string ->
                    player.sendMessage(StringUtils.alignCenter(ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, string)));
            player.sendMessage("");
            player.sendMessage(lang.getString("match.ingame.prepare-change-phase"));
        });

        this.bombScoreBar.removeAll();
        this.bombDefusingBar.removeAll();
    }

    private class BombCartMechanicTask extends AsyncTimerTask {

        private final String mechanicID;
        private final BombCartMechanic mechanic;
        private int clock = 0;
        private Location lookForwardLocation;
        private TeamRole winningTeam;

        private double defusingTime = DEFUSING_TIME;
        private boolean recentlyDefusing = false;
        private ExplosiveMinecart minecart;

        private AtomicReference<ExplosiveMinecart> explosiveMinecart = new AtomicReference<>();

        private int atkWin = 0, defWin = 0;

        public BombCartMechanicTask(BombCartMechanic mechanic) {
            super(TimeUnit.SECONDS, 1, mechanic.getID() + "-" + getIndex());

            this.mechanicID = mechanic.getID();
            bombDefusingBarPattern = lang.getString("mechanic." + mechanicID + ".defusing-bar-title");

            MatchScore score = getMatch().getMatchScore();
            this.atkWin = score.getScore(true, TeamRole.ATTACK);
            this.defWin = score.getScore(true, TeamRole.DEFEND);

            Future<ExplosiveMinecart> explosiveMinecartFuture = Bukkit.getScheduler().callSyncMethod(Aradite.getInstance(), () -> {
                ExplosiveMinecart minecart = cartStart.getWorld().spawn(cartStart, ExplosiveMinecart.class);
                minecart.setInvulnerable(true);
                minecart.setCustomName(lang.getString("mechanic." + getID() + ".cart-name"));
                minecart.setCustomNameVisible(true);
                return minecart;
            });

            try {
                this.minecart = explosiveMinecartFuture.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            this.mechanic = mechanic;
        }

        @Override
        public void onExecute() {
            this.lookForwardLocation = lookTowardsLocation(cartStart, cartEnd);

            bombScoreBar = Bukkit.createBossBar(
                    bombScoreBarPattern.replace("%attacker_score%", "" + atkWin)
                            .replace("%defender_score%", "" + defWin)
                            .replace("%countdown_time%", formatCountdown()),
                    BarColor.YELLOW, BarStyle.SEGMENTED_10, BarFlag.PLAY_BOSS_MUSIC);
            getMatch().getMatchTeam().getAllPlayers().forEach(player -> {
                bombScoreBar.addPlayer(player);
            });

            bombDefusingBar = Bukkit.createBossBar(bombDefusingBarPattern, BarColor.PINK, BarStyle.SOLID,
                    BarFlag.PLAY_BOSS_MUSIC);

            List<Player> defenders = getMatch().getMatchTeam().getTeam(TeamRole.DEFEND).getMembers();
            defenders.forEach(def -> def.getInventory().setItem(0, DEFUSING_ITEM));
        }

        @Override
        public void run() {
            clock++;

            handleStop(cartEnd);
            handleNearbyPlayer(lookForwardLocation);

            bombScoreBar.setTitle(bombScoreBarPattern.replace("%attacker_score%", "" + atkWin)
                    .replace("%defender_score%", "" + defWin)
                    .replace("%countdown_time%", formatCountdown()));
            double percent = PrimaryMath.percentage(clock, TOTAL_TIME_TO_PUSH, PrimaryMath.PercentageMode.ONE_DIGIT);
            bombScoreBar.setProgress(1d - (percent / 100));
        }

        private String formatCountdown() {
            long millis = TimeUnit.SECONDS.toMillis(TOTAL_TIME_TO_PUSH - clock);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            return hms;
        }

        /**
         * Check near-by players. If there are only attackers, push the mine-cart. Otherwise, do nothing.
         *
         * @param lookForwardLocation The going-to-go direction.
         */
        private void handleNearbyPlayer(Location lookForwardLocation) {
            Match match = getMatch();
            MatchTeam matchTeam = match.getMatchTeam();
            MatchScore matchScore = match.getMatchScore();

            /*
             * Check surround players.
             */
            List<Player> surroundPlayers = new ArrayList<>(
                    minecart.getLocation().getNearbyPlayers(PUSH_MINECART_RANGE));
            Player mocker = surroundPlayers.stream().filter(player -> matchTeam.getPlayerTeam(player) == TeamRole.DEFEND)
                    .findAny().orElse(null);
            if (mocker != null || surroundPlayers.isEmpty()) {
                minecart.setMaxSpeed(0);
                return;
            } // Having at least one defender in range then stop.

            minecart.setMaxSpeed(0.1);
            controlCartMovement();

            surroundPlayers.forEach(player -> {
                PlayerInGameMVPPacket mvpPacket = (PlayerInGameMVPPacket) getMatch().retrieveProtocol(player)
                        .getPacket(PacketType.INGAME_MVP);
                mvpPacket.addMVPPoint(PlayerInGameMVPPacket.MVPStatistics.PUSH_CART, 3);
            });

        }

        /**
         * Check the stop state of the mine-cart. (Winning condition).
         *
         * @param goal The going-to-go location
         */
        private void handleStop(Location goal) {
            MatchScore matchScore = getMatch().getMatchScore();

            if (recentlyDefusing && defusingTime <= 0) {
                cancel();

                matchScore.setScore(3 + getIndex(), TeamRole.DEFEND, true);
                this.winningTeam = TeamRole.DEFEND;

                minecart.getWorld().spawnParticle(Particle.SMOKE_LARGE, minecart.getLocation().clone().add(0, 0.5, 0),
                        2, 0.05, 0.05, 0.05);
                minecart.setDisplayBlockData(Material.AIR.createBlockData());

                winningReasons.add(WinningReason.DEFENDER_BOMB_DEFUSE);

                mechanic.finish();
                minecart.remove();
                return;
            }

            if (TOTAL_TIME_TO_PUSH - clock <= 0) {
                cancel();

                matchScore.setScore(3 + getIndex(), TeamRole.ATTACK, true);
                this.winningTeam = TeamRole.DEFEND;

                minecart.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, minecart.getLocation().clone().add(0, 0.5, 0),
                        2, 0.05, 0.05, 0.05);
                minecart.remove();

                winningReasons.add(WinningReason.DEFENDER_BOMB_EXPLODE);

                finish();
                minecart.remove();
                return;
            }

            MatchTeam matchTeam = getMatch().getMatchTeam();
            Player defuser = this.minecart.getLocation().getNearbyPlayers(PUSH_MINECART_RANGE)
                    .stream().filter(player -> {
                        boolean sneak = player.isSneaking();
                        ItemStack inHandItem = player.getInventory().getItemInMainHand();
                        return sneak && ItemCheckFactory.of(DEFUSING_ITEM).compare(inHandItem);
                    }).findAny().orElse(null);
            if (defuser != null) {
                matchTeam.getAllPlayers().forEach(player -> {
                    bombDefusingBar.addPlayer(player);
                });

                double defusePercent = PrimaryMath.percentage(this.defusingTime, DEFUSING_TIME, PrimaryMath.PercentageMode.ONE_DIGIT);
                defusePercent = defusePercent / 100;
                bombDefusingBar.setProgress(defusePercent);
                bombDefusingBar.setTitle(bombDefusingBarPattern.replaceAll("%countdown%", defusingTime + ""));

                PlayerInGameMVPPacket mvpPacket = (PlayerInGameMVPPacket) getMatch().retrieveProtocol(defuser)
                        .getPacket(PacketType.INGAME_MVP);
                mvpPacket.addMVPPoint(PlayerInGameMVPPacket.MVPStatistics.DEFUSE_BOMB, 8);

                this.defusingTime--;
                if (!recentlyDefusing) this.recentlyDefusing = true;
                return;
            } else {
                if (recentlyDefusing) {
                    bombDefusingBar.removeAll();
                    this.defusingTime = DEFUSING_TIME;
                    this.recentlyDefusing = false;
                }
            }

            if (minecart.getLocation().distanceSquared(goal) <= 0.5) {
                cancel();

                matchScore.setScore(3 + getIndex(), TeamRole.ATTACK, true);
                this.winningTeam = TeamRole.ATTACK;

                minecart.getWorld().spawnParticle(Particle.TOTEM, minecart.getLocation().clone().add(0, 0.5, 0), 8, 0.02,
                        0.02, 0.02);
                minecart.remove();

                winningReasons.add(WinningReason.ATTACKER_PUSH_SUCCESS);

                finish();
                minecart.remove();
            }
        }

        /**
         * Control the movement of the mine-cart
         */
        private void controlCartMovement() {
            if ((Math.abs(minecart.getVelocity().getX()) + Math.abs(minecart.getVelocity().getY())
                    + Math.abs(minecart.getVelocity().getZ())) > 0.1) {
                return;
            }
            // Minimum speed (0.1)
            if ((Math.abs(minecart.getVelocity().getX()) + Math.abs(minecart.getVelocity().getY())
                    + Math.abs(minecart.getVelocity().getZ())) < 0.02) {
                double oldY = minecart.getVelocity().getY();
                try {
                    minecart.setVelocity(minecart.getVelocity().multiply(0.1 / (Math.abs(minecart.getVelocity().getX())
                            + Math.abs(minecart.getVelocity().getY()) + Math.abs(minecart.getVelocity().getZ()))));
                    minecart.setVelocity(minecart.getVelocity().setY(oldY));
                } catch (Exception e) { // In case getX, getY, getZ return 0
                    // TODO: handle exception
                }
            }

            double oldY = minecart.getVelocity().getY();
            minecart.setVelocity(minecart.getVelocity().multiply(1 + (0.05 * 2)));
            minecart.setVelocity(minecart.getVelocity().setY(oldY));
        }

        // the lookTowardsLocation method (it is called lookTowardsLocation because i
        // copy-pasted it from my other project), it sets pitch & yaw

        /**
         * Calculate the velocity for the minecart.
         */
        private Location lookTowardsLocation(Location from, Location to) {
            if (from != null && to != null) {
                Location loc = from.clone();
                double x = to.getX() - from.getX();
                double y = to.getY() - from.getY();
                double z = to.getZ() - from.getZ();
                if (x == 0 && z == 0) {
                    loc.setPitch(y > 0 ? -90 : 90);
                    return loc;
                }
                loc.setYaw((float) Math.toDegrees((Math.atan2(-x, z) + (Math.PI * 2)) % (Math.PI * 2)));
                loc.setPitch((float) Math.toDegrees(Math.atan(-y / Math.sqrt((x * x) + (z * z)))));
                return loc;
            }
            return null;
        }

    }

    private enum WinningReason {
        ATTACKER_PUSH_SUCCESS("§c💣"),
        DEFENDER_BOMB_EXPLODE("§b💥"),
        DEFENDER_BOMB_DEFUSE("§b✂");

        private final String icon;

        private WinningReason(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }


}
