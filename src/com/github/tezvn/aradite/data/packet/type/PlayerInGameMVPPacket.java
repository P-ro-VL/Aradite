package com.github.tezvn.aradite.data.packet.type;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.data.Statistic;
import com.github.tezvn.aradite.data.packet.Packet;
import com.github.tezvn.aradite.data.packet.PacketPackage;
import com.github.tezvn.aradite.match.MedalRank;
import com.github.tezvn.aradite.match.mechanic.MechanicType;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import pdx.mantlecore.math.PrimaryMath;

import java.util.stream.Collectors;

public class PlayerInGameMVPPacket implements Packet<String> {

    private Player player;
    private PacketPackage packet;

    public PlayerInGameMVPPacket(Player player) {
        this.player = player;
        this.packet = new PacketPackage<>();
    }

    @Override
    public Player getPacketOwner() {
        return this.player;
    }

    @Override
    public PacketPackage<String> getPacketContents() {
        return this.packet;
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public void deserialize(String string) {
    }

    /**
     * Return the currently total MVP points of the player.
     */
    public double getTotalMVPPoint() {
        PacketPackage<String> packetPackage = getPacketContents();
        return PrimaryMath.sum(double.class,
                packetPackage.getMapData().values().stream().map(string -> Double.parseDouble(string))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Return the mvp point of a specific statistic.
     *
     * @param statistics The statistic
     * @return The MVP point
     */
    public double getMVPPoint(MVPStatistics statistics) {
        return Double.parseDouble(getPacketContents().getMapData().getOrDefault(statistics, "0.0"));
    }

    /**
     * Change the currently total MVP points of the player.
     *
     * @param mvpPoint New MVP points.
     */
    public void setMVPPoint(MVPStatistics mvpStatistics, double mvpPoint) {
        getPacketContents().write(mvpStatistics.toString(), "" + mvpPoint);
    }

    /**
     * Add a specific amount of mvp points.
     *
     * @param statistics The MVP statistic
     * @param mvpPoint   Point to add
     */
    public void addMVPPoint(MVPStatistics statistics, double mvpPoint) {
        double currentPoints = getMVPPoint(statistics);
        setMVPPoint(statistics, currentPoints + mvpPoint);
    }

    /**
     * Return the value of a specific statistic.
     * @param statistic The statistic
     */
    public int getStatistic(Statistic statistic){
        if(!getPacketContents().getMapData().containsKey(statistic.toString())) return 0;
        return (int) Double.parseDouble(getPacketContents().getMapData().getOrDefault(statistic.toString(),
                "0"));
    }

    /**
     * Set the statistic data value.
     * @param statistic The statistic
     * @param value The value
     */
    public void setStatistic(Statistic statistic, int value){
        getPacketContents().write(statistic.toString(), value + "");
    }

    /**
     * Increase the value of the given statistic by one.
     * @param statistic The statistic
     */
    public void increaseByOne(Statistic statistic){
        int value = getStatistic(statistic);
        setStatistic(statistic, value + 1);
    }

    public static enum MVPStatistics {
        /**
         * Point that will be added to players when one phase has completed.<br>
         * Winning team get more point than that of losing team.
         */
        MATCH_POINT(10, 15, 20, 25),

        /**
         * Point that players receive when capturing the capture point
         * in {@link MechanicType#CAPTURE capture mode}.
         */
        CAPTURE_POINT(185, 195, 220, 250),

        /**
         * Point that attacker players receive when pushing the bomb cart.
         */
        PUSH_CART(300, 450, 540, 600),

        /**
         * Point that defender players receive when defusing the bomb successfully.
         */
        DEFUSE_BOMB(90, 150, 230, 320),

        KILL(6, 8, 10, 12);

        private final int[] ranks;

        private MVPStatistics(int... ranks) {
            this.ranks = ranks;
        }

        /**
         * Return the rank player will achieve with the given {@code point}.
         *
         * @param point The point
         * @return The relative rank.
         */
        public MedalRank getRankWithGivenPoint(double point) {
            MedalRank rank = null;
            for (MedalRank r : MedalRank.values()) {
                if (point >= getPointToGet(r)) rank = r;
            }
            return rank;
        }

        /**
         * Return the breakthrough point that player needs to reach to get the given {@code rank} of this stats.
         *
         * @param rank The rank
         * @return The least point.
         */
        public int getPointToGet(MedalRank rank) {
            return ranks[rank.ordinal()];
        }

        /**
         * Return the medal icon of the stats.
         */
        public String getMedalIcon() {
            return Aradite.getInstance().getLanguage().getString("medal." + toString().toLowerCase() + ".icon");
        }

        /**
         * Return the description of the statistic.
         */
        public String getDescription(){
            return Aradite.getInstance().getLanguage().getString("medal." + toString().toLowerCase()
                    + ".description");
        }
    }

}
