package com.github.tezvn.aradite.impl.data.packet.type;

import com.github.tezvn.aradite.api.agent.Agent;
import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.type.PlayerPreGameSelectPacket;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.data.packet.AbstractPacket;
import com.github.tezvn.aradite.impl.data.packet.DefaultPacketPackage;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.impl.weapon.WeaponManager;
import com.github.tezvn.aradite.api.weapon.WeaponType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPreGameSelectPacketImpl extends AbstractPacket<String> implements PlayerPreGameSelectPacket {

    public PlayerPreGameSelectPacketImpl(Player player) {
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
        return AraditeImpl.getInstance().getAgentManager().createNewInstance(getSelectedAgentType());
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

        WeaponManager weaponManager =  AraditeImpl.getInstance().getWeaponManager();
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
