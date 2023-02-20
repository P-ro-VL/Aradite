package com.github.tezvn.aradite.api.packet.type;

import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.weapon.Weapon;
import org.bukkit.entity.Player;

public interface PlayerInGameLastDamagePacket extends Packet<String> {

    /**
     * Add an amount of damage dealt.
     */
    public void addDamageDealt(double value);

    /**
     * Return the total damage the packet owner dealt on others.
     */
    public double getTotalDamageDealt();

    /**
     * Return the last reason player died.
     */
    public DeathReason getLastDeathReason();

    /**
     * Return the player who last killed the packet's owner.
     */
    public Player getLastKiller();

    /**
     * Return the weapon that the last killer used.
     */
    public Weapon getLastKilledWeapon();

    /**
     * Record the last death data.
     *
     * @param reason   The death reason
     * @param killer   The killer
     * @param weaponID The id of the weapon killer used
     */
    public void setDeath(DeathReason reason, Player killer, String weaponID);

    /**
     * Record the death by skill's damage data.
     *
     * @param skiller The killer
     * @param skill   The skill killer used
     */
    public void setDeath(Player skiller, Skill skill);

    /**
     * Return the dead status of the player.
     */
    public boolean isDead();

    /**
     * Change the dead status of the player.
     *
     * @param dead The new dead status
     */
    public void setDead(boolean dead);

    /**
     * Return the skill name killed player.
     */
    public String getLastKilledSkill();

    public static enum DeathReason {
        GUN,
        KNIFE,
        GRANDE, SKILL;
    }

}
