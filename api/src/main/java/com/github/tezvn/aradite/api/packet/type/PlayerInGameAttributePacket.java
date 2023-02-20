package com.github.tezvn.aradite.api.packet.type;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.attribute.Attribute;
import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import org.bukkit.Bukkit;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public interface PlayerInGameAttributePacket extends Packet<Double>  {

    /**
     * Set the attribute values for player.
     *
     * @param attribute The attribute pack.
     */
    void setAttributePack(Attribute attribute);

    /**
     * Return the current attribute of the player.
     *
     * @param attributeType The attribute type
     * @return The attribute value
     */
    double getAttribute(AttributeType attributeType);

    /**
     * Change the value of a specific attribute type.
     *
     * @param attributeType The attribute type
     * @param value         The new value
     */
    void setAttribute(AttributeType attributeType, double value);

    /**
     * Do a specific amount of damage to player.
     *
     * @param damager Damage source.
     * @param damage  Damage amount
     * @param crit    {@code true} if it's the critical damage.
     */
    void damage(String damager, double damage, boolean crit, PlayerInGameLastDamagePacket lastDmg);

}
