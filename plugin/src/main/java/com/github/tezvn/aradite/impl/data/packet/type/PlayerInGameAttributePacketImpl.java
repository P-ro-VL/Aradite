package com.github.tezvn.aradite.impl.data.packet.type;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.attribute.Attribute;
import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.data.packet.AbstractPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class PlayerInGameAttributePacketImpl extends AbstractPacket<Double> implements PlayerInGameAttributePacket {

    public PlayerInGameAttributePacketImpl(Player player) {
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
     * Set the attribute values for player.
     *
     * @param attribute The attribute pack.
     */
    public void setAttributePack(Attribute attribute) {
        PacketPackage<Double> packetPackage = getPacketContents();

        for (Map.Entry<AttributeType, Double> attributeType : attribute.getAttributeMap().entrySet()) {
            packetPackage.getMapData().put(attributeType.getKey().toString(), attributeType.getValue());
        }
    }

    /**
     * Return the current attribute of the player.
     *
     * @param attributeType The attribute type
     * @return The attribute value
     */
    public double getAttribute(AttributeType attributeType) {
        return getPacketContents().getMapData().getOrDefault(attributeType.toString(), 0d);
    }

    /**
     * Change the value of a specific attribute type.
     *
     * @param attributeType The attribute type
     * @param value         The new value
     */
    public void setAttribute(AttributeType attributeType, double value) {
        getPacketContents().write(attributeType.toString(), value);
    }

    /**
     * Do a specific amount of damage to player.
     *
     * @param damager Damage source.
     * @param damage  Damage amount
     * @param crit    {@code true} if it's the critical damage.
     */
    public void damage(String damager, double damage, boolean crit, PlayerInGameLastDamagePacket lastDmg) {
        double currentHP = getAttribute(AttributeType.CURRENT_HEALTH);

        double armor = getAttribute(AttributeType.ARMOR);
        double armorPenetration = getAttribute(AttributeType.ARMOR_PENETRATION);

        double finalDamage = damage - (armor / 100d) * 30d + (armor / 100d) * armorPenetration;

        double reducePercent = getAttribute(AttributeType.DAMAGE_REDUCE_IN_PERCENT);
        if (reducePercent > 0) {
            finalDamage = finalDamage - (finalDamage / 100) * reducePercent;
        }

        boolean hasCritted = false;
        if (crit) {
            double critChance = getAttribute(AttributeType.CRITICAL_CHANCE);

            if (ThreadLocalRandom.current().nextInt(100) < critChance) {
                hasCritted = true;
                double critMultiply = getAttribute(AttributeType.CRITICAL_MULTIPLY);
                finalDamage = finalDamage * critMultiply;
            }
        }

        double remainHealth = currentHP - finalDamage;
        if (remainHealth <= 0) {
            String[] arg0 = damager.split(Pattern.quote(":"));
            PlayerInGameLastDamagePacketImpl.DeathReason deathReason = PlayerInGameLastDamagePacketImpl.DeathReason.valueOf(arg0[0]);
            String filteredReasonOut = arg0[1];
            String[] arg1 = filteredReasonOut.split("•");

            String killerName = arg1[0];
            if (deathReason == PlayerInGameLastDamagePacketImpl.DeathReason.SKILL) {
                Agents agentType = Agents.valueOf(arg1[1].toUpperCase(Locale.ROOT));
                SkillType skillType = SkillType.valueOf(arg1[2]);
                lastDmg.setDeath(Bukkit.getPlayer(killerName),
                        AraditeImpl.getInstance().getAgentManager().createNewInstance(agentType).getSkills().get(skillType));
            } else {
                String weaponID = arg1[1];

                lastDmg.setDeath(deathReason, Bukkit.getPlayer(killerName),
                        weaponID);
            }
        }

        setAttribute(AttributeType.CURRENT_HEALTH, Math.max(0, remainHealth));

        lastDmg.addDamageDealt(finalDamage);

        PacketPackage<String> lastDmgPacket = lastDmg.getPacketContents();
        lastDmgPacket.write("" + System.currentTimeMillis(),
                damager + "--" + finalDamage + "--" + damage + "--" + hasCritted);
    }

}
