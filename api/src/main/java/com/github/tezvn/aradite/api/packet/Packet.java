package com.github.tezvn.aradite.api.packet;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Packets are data that is categorized and compressed to store information
 * about specific data field.<br>
 * Packets can be serialize and deserialize for backuping and storing data in
 * databases.
 * 
 * @author phongphong28
 */
public interface Packet<T> {

	/**
	 * Player who sent or obssessed the packet.
	 */
	Player getPacketOwner();

	/**
	 * All data the packet holds.
	 */
	PacketPackage<T> getPacketContents();

	/**
	 * Serialize the packet as {@link String}.
	 */
	String serialize();

	/**
	 * Deserialize the packet from the given {@code string}<br>
	 * The deserialized data must be in {@link Map} form and set directly to
	 * {@link #getPacketContents()}.
	 * 
	 * @param string
	 *            Raw data
	 */
	void deserialize(String string);

}
