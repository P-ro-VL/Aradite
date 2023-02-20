package com.github.tezvn.aradite.api.data;

import com.github.tezvn.aradite.api.agent.Agents;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public interface PlayerData {

    /**
     * Return the unique ID of the player.
     */
    public UUID getUniqueId();

    /**
     * Return the amount of times that player selects the given {@code agent}.
     */
    public int getAgentPickData(Agents agent);

    /**
     * Increase the times player has selected the given {@code agent} by 1.
     * @param agent The agent.
     */
    public void increaseAgentPickData(Agents agent);

    /**
     * Change the date that player created his account (in server).
     *
     * @param registerAccountDate New date (in millis)
     */
    public void setRegisterAccountDate(long registerAccountDate);

    /**
     * Return the date (in millis) when player created his account (in server).
     */
    public long getRegisterAccountDate();

    /**
     * Return the statistic of a specific approach identified by the given
     * {@code key}.
     *
     * @param key Statistic key
     * @return Statistic
     */
    public int getStatistic(EnumDataKey key);

    /**
     * Add {@code value} to the statistic indentified by the given {@code key}.
     *
     * @param key   Statistic's key
     * @param value Value to be added
     */
    public void increase(EnumDataKey key, int value);

    /**
     * Minus {@code value} from the statistic indentified by the given {@code key}.
     *
     * @param key           Statistic's key
     * @param value         Value to be removed
     * @param canBeNegative {@code true} if new statistic value can be negative.
     */
    public void decrease(EnumDataKey key, int value, boolean canBeNegative);

    /**
     * Return the mastery point of a specific agent.
     *
     * @param agent The agent
     * @return The mastery point
     */
    public int getMastery(Agents agent);

    /**
     * Change the current mastery point of a specific agent.
     *
     * @param agent        The agent
     * @param masteryPoint New mastery point. (must be equal or greater than 0)
     */
    public void setMastery(Agents agent, int masteryPoint);

    /**
     * Add a specific amount mastery point to an agent.
     *
     * @param agent        The agent
     * @param masteryPoint The additional point. It can be negative.
     */
    public void addMastery(Agents agent, int masteryPoint);

}
