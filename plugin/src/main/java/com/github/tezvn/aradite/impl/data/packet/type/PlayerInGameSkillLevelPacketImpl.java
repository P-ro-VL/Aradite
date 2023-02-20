package com.github.tezvn.aradite.impl.data.packet.type;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameSkillLevelPacket;
import com.github.tezvn.aradite.impl.data.packet.AbstractPacket;
import com.github.tezvn.aradite.impl.data.packet.DefaultPacketPackage;
import org.bukkit.entity.Player;

public class PlayerInGameSkillLevelPacketImpl extends AbstractPacket<Integer> implements PlayerInGameSkillLevelPacket {

	public PlayerInGameSkillLevelPacketImpl(Player player, Agents agent) {
		super(player);
		this.getPacketContents().write(agent.toString().toLowerCase() + "-activate-1", 1);
		this.getPacketContents().write(agent.toString().toLowerCase() + "-activate-2", 1);
		this.getPacketContents().write(agent.toString().toLowerCase() + "-ultimate", 1);
	}

	@Override
	public String serialize() {
		return null;
	}

	@Override
	public void deserialize(String string) {
	}

}
