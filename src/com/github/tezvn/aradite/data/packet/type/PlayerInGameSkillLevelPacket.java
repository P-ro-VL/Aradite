package com.github.tezvn.aradite.data.packet.type;

import com.github.tezvn.aradite.agent.Agents;
import org.bukkit.entity.Player;

import com.github.tezvn.aradite.data.packet.Packet;
import com.github.tezvn.aradite.data.packet.PacketPackage;

public class PlayerInGameSkillLevelPacket implements Packet<Integer> {

	private Player player;
	private PacketPackage packet;

	public PlayerInGameSkillLevelPacket(Player player, Agents agent) {
		this.player = player;
		this.packet = new PacketPackage<>();

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
