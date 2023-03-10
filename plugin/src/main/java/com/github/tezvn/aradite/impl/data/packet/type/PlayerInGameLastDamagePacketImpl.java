package com.github.tezvn.aradite.impl.data.packet.type;

import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.data.packet.AbstractPacket;
import com.github.tezvn.aradite.impl.data.packet.DefaultPacketPackage;
import com.github.tezvn.aradite.impl.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerInGameLastDamagePacketImpl extends AbstractPacket<String> implements PlayerInGameLastDamagePacket {

    public PlayerInGameLastDamagePacketImpl(Player player) {
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
     * Add an amount of damage dealt.
     */
    public void addDamageDealt(double value) {
        double currentTotal = getTotalDamageDealt();
        getPacketContents().getMapData().put("total-damage-dealt", "" + (currentTotal + value));
    }

    /**
     * Return the total damage the packet owner dealt on others.
     */
    public double getTotalDamageDealt() {
        return Double.parseDouble(getPacketContents().getMapData().getOrDefault("total-damage-dealt",
                "0"));
    }

    /**
     * Return the last reason player died.
     */
    public DeathReason getLastDeathReason() {
        return DeathReason.valueOf(getPacketContents().getMapData().get("last-death.reason"));
    }

    /**
     * Return the player who last killed the packet's owner.
     */
    public Player getLastKiller() {
        String lastKillerUUID = getPacketContents().getMapData().get("last-death.killer");
        if (lastKillerUUID.equalsIgnoreCase("unknown")) return null;
        Player player = Bukkit.getPlayer(UUID.fromString(lastKillerUUID));
        if (player == null || !player.isOnline() || !player.isValid()) return null;
        return player;
    }

    /**
     * Return the weapon that the last killer used.
     */
    public Weapon getLastKilledWeapon() {
        String weaponId = getPacketContents().getMapData().get("last-death.weapon");
        if (weaponId == null || weaponId.isEmpty()) return null;
        WeaponManager weaponManager = AraditeImpl.getInstance().getWeaponManager();
        return weaponManager.getWeaponByID(weaponId);
    }

    /**
     * Record the last death data.
     *
     * @param reason   The death reason
     * @param killer   The killer
     * @param weaponID The id of the weapon killer used
     */
    public void setDeath(DeathReason reason, Player killer, String weaponID) {
        PacketPackage<String> content = getPacketContents();
        content.write("last-death.reason", reason.toString());
        content.write("last-death.killer", killer == null ? "unknown" : killer.getUniqueId().toString());
        content.write("last-death.weapon", weaponID);
    }

    /**
     * Record the death by skill's damage data.
     *
     * @param skiller The killer
     * @param skill   The skill killer used
     */
    public void setDeath(Player skiller, Skill skill) {
        PacketPackage<String> content = getPacketContents();
        content.write("last-death.reason", DeathReason.SKILL.toString());
        content.write("last-death.killer", skiller == null ? "unknown" : skiller.getUniqueId().toString());
        content.write("last-death.skill", skill.getDisplayName());
    }

    /**
     * Return the dead status of the player.
     */
    public boolean isDead() {
        return Boolean.parseBoolean(getPacketContents().getSingleData());
    }

    /**
     * Change the dead status of the player.
     *
     * @param dead The new dead status
     */
    public void setDead(boolean dead) {
        getPacketContents().setSingleData("" + dead);
    }

    /**
     * Return the skill name killed player.
     */
    public String getLastKilledSkill() {
        return getPacketContents().getMapData().getOrDefault("last-death.skill", null);
    }

}
