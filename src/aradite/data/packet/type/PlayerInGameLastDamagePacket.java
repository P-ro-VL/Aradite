package aradite.data.packet.type;

import aradite.Aradite;
import aradite.agent.skill.Skill;
import aradite.weapon.Weapon;
import aradite.weapon.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import aradite.data.packet.Packet;
import aradite.data.packet.PacketPackage;

import java.util.UUID;

public class PlayerInGameLastDamagePacket implements Packet<String> {

    private Player player;
    private PacketPackage packet;

    public PlayerInGameLastDamagePacket(Player player) {
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
     * Add an amount of damage dealt.
     */
    public void addDamageDealt(double value) {
        double currentTotal = getTotalDamageDealth();
        getPacketContents().getMapData().put("total-damage-dealt", "" + (currentTotal + value));
    }

    /**
     * Return the total damage the packet owner dealt on others.
     */
    public double getTotalDamageDealth() {
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
        WeaponManager weaponManager = Aradite.getInstance().getWeaponManager();
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
     * @param skiller The killer
     * @param skill The skill killer used
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

    public static enum DeathReason {
        GUN,
        KNIFE,
        GRANDE, SKILL;
    }

}
