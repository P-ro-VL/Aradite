package com.github.tezvn.aradite.impl.data.packet.type;

import com.github.tezvn.aradite.api.packet.PacketPackage;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameMVPPacket;
import com.github.tezvn.aradite.api.data.Statistic;
import com.github.tezvn.aradite.impl.data.packet.AbstractPacket;
import com.github.tezvn.aradite.impl.data.packet.DefaultPacketPackage;
import org.bukkit.entity.Player;
import pdx.mantlecore.math.PrimaryMath;

import java.util.stream.Collectors;

public class PlayerInGameMVPPacketImpl extends AbstractPacket<String> implements PlayerInGameMVPPacket {

    public PlayerInGameMVPPacketImpl(Player player) {
        super(player);
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

}
