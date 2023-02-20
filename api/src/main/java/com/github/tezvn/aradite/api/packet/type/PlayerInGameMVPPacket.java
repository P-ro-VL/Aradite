package com.github.tezvn.aradite.api.packet.type;

import com.github.tezvn.aradite.api.AraditeAPIProvider;
import com.github.tezvn.aradite.api.data.Statistic;
import com.github.tezvn.aradite.api.match.MedalRank;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public interface PlayerInGameMVPPacket extends Packet<String> {

    /**
     * Return the currently total MVP points of the player.
     */
    double getTotalMVPPoint();

    /**
     * Return the mvp point of a specific statistic.
     *
     * @param statistics The statistic
     * @return The MVP point
     */
    double getMVPPoint(MVPStatistics statistics);

    /**
     * Change the currently total MVP points of the player.
     *
     * @param mvpPoint New MVP points.
     */
    void setMVPPoint(MVPStatistics mvpStatistics, double mvpPoint);

    /**
     * Add a specific amount of mvp points.
     *
     * @param statistics The MVP statistic
     * @param mvpPoint   Point to add
     */
    void addMVPPoint(MVPStatistics statistics, double mvpPoint);

    /**
     * Return the value of a specific statistic.
     * @param statistic The statistic
     */
    int getStatistic(Statistic statistic);

    /**
     * Set the statistic data value.
     * @param statistic The statistic
     * @param value The value
     */
    void setStatistic(Statistic statistic, int value);

    /**
     * Increase the value of the given statistic by one.
     * @param statistic The statistic
     */
    void increaseByOne(Statistic statistic);

    enum MVPStatistics {
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

        MVPStatistics(int... ranks) {
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
            return  AraditeAPIProvider.get().getLanguage().getString("medal." + toString().toLowerCase() + ".icon");
        }

        /**
         * Return the description of the statistic.
         */
        public String getDescription(){
            return  AraditeAPIProvider.get().getLanguage().getString("medal." + toString().toLowerCase()
                    + ".description");
        }
    }
}
