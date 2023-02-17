package com.github.tezvn.aradite.data.packet.type;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.Agent;
import com.github.tezvn.aradite.weapon.Weapon;
import com.github.tezvn.aradite.weapon.WeaponManager;
import com.github.tezvn.aradite.weapon.WeaponType;
import com.github.tezvn.aradite.agent.Agents;
import com.github.tezvn.aradite.data.packet.Packet;
import com.github.tezvn.aradite.data.packet.PacketPackage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPreGameSelectPacket implements Packet<String> {

    private Player player;
    private PacketPackage<String> packet;

    public PlayerPreGameSelectPacket(Player player) {
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
     * Return the agent enum that player has selected.
     */
    public Agents getSelectedAgentType() {
        String agentID = getPacketContents().getMapData().get("agent-type");
        if (agentID == null) return null;
        return Agents.valueOf(agentID.toUpperCase());
    }

    /**
     * Return the agent instance that player has selected.
     */
    public Agent getSelectedAgent() {
        Agents type = getSelectedAgentType();
        Class<?> wrapper = type.getWrapper();
        try {
            Agent agent = (Agent) wrapper.newInstance();
            return agent;
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Cannot create new instance for agent type : " + type.toString());
        }
        return null;
    }

    /**
     * Return the selected weapon basing on given type.
     *
     * @param weaponType The weapon type.
     * @return The weapon
     */
    public Weapon getSelectedWeapon(WeaponType weaponType) {
        String type = weaponType.toString().toLowerCase();

        String weaponId = getPacketContents().getMapData().get(type + "-weapon");
        if (weaponId == null || weaponId.isEmpty()) return null;

        WeaponManager weaponManager = Aradite.getInstance().getWeaponManager();
        return weaponManager.getWeaponByID(weaponId);
    }

    /**
     * Return the skin of the given weapon type player has selected.
     * @param weaponType The weapon type
     */
    public String getSelectedWeaponSkin(WeaponType weaponType){
        return getPacketContents().getMapData().getOrDefault(weaponType.toString().toLowerCase() + "-weapon-skin",
                "default");
    }

    /**
     * Set the selected weapon for the given weapon type.
     * @param weaponType The weapon type
     * @param weapon The weapon
     */
    public void setSelectedWeapon(WeaponType weaponType, Weapon weapon) {
        getPacketContents().write(weaponType.toString().toLowerCase() + "-weapon", weapon.getID());
    }
}
