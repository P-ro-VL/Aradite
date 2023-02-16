package aradite.data.global;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import aradite.data.EnumDataKey;
import aradite.data.SQLTarget;
import aradite.data.Statistic;
import com.google.common.collect.Maps;

import aradite.agent.Agents;
import org.bukkit.entity.Player;

/**
 * Each {@link PlayerDataStorage} represents for a specific player's data.<br>
 * We store all data from match data to event data in order build up a complete
 * database for VALORANT. Some data may be stored in local database, some
 * queriable ones will be uploaded to SQL database for better and distance
 * control.
 *
 * @author phongphong28
 */
public class PlayerDataStorage implements SQLTarget {

    private UUID uuid;
    private long registerAccountDate;

    private Map<EnumDataKey, AtomicInteger> statistics = Maps.newHashMap();

    private Map<Agents, Integer> agentMastery = Maps.newHashMap();

    private Map<Agents, AtomicInteger> agentPickData = Maps.newHashMap();

    public PlayerDataStorage(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerDataStorage(Player player) {
        this.uuid = player.getUniqueId();
    }

    /**
     * Return the unique ID of the player.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Return the amount of times that player selects the given {@code agent}.
     */
    public int getAgentPickData(Agents agent) {
        agentPickData.putIfAbsent(agent, new AtomicInteger(0));
        AtomicInteger atomicInteger = agentPickData.get(agent);
        return atomicInteger.get();
    }

    /**
     * Increase the times player has selected the given {@code agent} by 1.
     * @param agent The agent.
     */
    public void increaseAgentPickData(Agents agent) {
        int times = getAgentPickData(agent);
		agentPickData.put(agent, new AtomicInteger(times++));
    }

    /**
     * Change the date that player created his account (in server).
     *
     * @param registerAccountDate New date (in millis)
     */
    public void setRegisterAccountDate(long registerAccountDate) {
        this.registerAccountDate = registerAccountDate;
    }

    /**
     * Return the date (in millis) when player created his account (in server).
     */
    public long getRegisterAccountDate() {
        return registerAccountDate;
    }

    /**
     * Return the statistic of a specific approach identified by the given
     * {@code key}.
     *
     * @param key Statistic key
     * @return Statistic
     * @see Statistic
     */
    public int getStatistic(EnumDataKey key) {
        return statistics.get(key).get();
    }

    /**
     * Add {@code value} to the statistic indentified by the given {@code key}.
     *
     * @param key   Statistic's key
     * @param value Value to be added
     */
    public void increase(EnumDataKey key, int value) {
        if (value < 0)
            throw new IllegalArgumentException("The increase value must be positive !");
        AtomicInteger currentValue = this.statistics.get(key);
        if (currentValue == null) {
            currentValue = new AtomicInteger(0);
            this.statistics.put(key, currentValue);
            currentValue = this.statistics.get(key);
        }
        currentValue.addAndGet(value);

    }

    /**
     * Minus {@code value} from the statistic indentified by the given {@code key}.
     *
     * @param key           Statistic's key
     * @param value         Value to be removed
     * @param canBeNegative {@code true} if new statistic value can be negative.
     */
    public void decrease(EnumDataKey key, int value, boolean canBeNegative) {
        AtomicInteger currentValue = this.statistics.get(key);
        if (currentValue == null) {
            currentValue = new AtomicInteger(0);
            this.statistics.put(key, currentValue);
            currentValue = this.statistics.get(key);
        }
        currentValue.addAndGet(-value);
        if (!canBeNegative && currentValue.get() < 0)
            currentValue.set(0);
    }

    /**
     * Return the mastery point of a specific agent.
     *
     * @param agent The agent
     * @return The mastery point
     */
    public int getMastery(Agents agent) {
        return this.agentMastery.getOrDefault(agent, 0);
    }

    /**
     * Change the current mastery point of a specific agent.
     *
     * @param agent        The agent
     * @param masteryPoint New mastery point. (must be equal or greater than 0)
     */
    public void setMastery(Agents agent, int masteryPoint) {
        if (masteryPoint < 0) throw new IllegalArgumentException("The mastery point must be equal or greater than 0 !");
        this.agentMastery.put(agent, masteryPoint);
    }

    /**
     * Add a specific amount mastery point to an agent.
     *
     * @param agent        The agent
     * @param masteryPoint The additional point. It can be negative.
     */
    public void addMastery(Agents agent, int masteryPoint) {
        int currentPoint = getMastery(agent);
        int newPoint = Math.max(0, currentPoint + masteryPoint);
        setMastery(agent, newPoint);
    }


}
