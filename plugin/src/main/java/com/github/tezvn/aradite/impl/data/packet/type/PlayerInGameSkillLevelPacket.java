package com.github.tezvn.aradite.impl.data.packet.type;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import com.github.tezvn.aradite.impl.data.packet.DefaultPacketPackage;
import org.bukkit.entity.Player;

public class PlayerInGameSkillLevelPacket implements Packet<Integer> {

	private Player player;
	private final PacketPackage<Integer> packet;

	public PlayerInGameSkillLevelPacket(Player player, Agents agent) {
		this.player = player;
		this.packet = new DefaultPacketPackage<>();

		this.packet.write(agent.toString().toLowerCase() + "-activate-1", 1);
		this.packet.write(agent.toString().toLowerCase() + "-activate-2", 1);
		this.packet.write(agent.toString().toLowerCase() + "-ultimate", 1);
	}

	@Override
	public Player getPacketOwner() {
		return this.player;
	}

	@Override
	public PacketPackage<Integer> getPacketContents() {
		return this.packet;
	}

	@Override
	public String serialize() {
		return null;
	}

	@Override
	public void deserialize(String string) {
	}

}
