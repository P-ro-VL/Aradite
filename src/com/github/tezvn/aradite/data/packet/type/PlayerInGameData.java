package com.github.tezvn.aradite.data.packet.type;

import java.util.Map;

import com.github.tezvn.aradite.data.packet.Packet;
import com.github.tezvn.aradite.data.packet.PacketType;
import com.google.common.collect.Maps;

public class PlayerInGameData {

    private Map<PacketType, Packet> packets = Maps.newHashMap();

    /**
     * Return the packet data whose type is {@code type}.
     *
     * @param type Packet type
     */
    public Packet getPacket(PacketType type) {
        return this.packets.get(type);
    }

    /**
     * Return the packet data whose class is {@code packetClass}
     * @param packetClass The class of the packet
     * @param <T> The packet class instance
     */
    public <T extends Packet> T getPacket(Class<T> packetClass) {
        for (Packet packet : packets.values()) {
			if(packet.getClass().getName().equalsIgnoreCase(packetClass.getName())) return (T) packet;
        }
        return null;
    }

    /**
     * Return all player's packet datas.
     */
    public Map<PacketType, Packet> getPackets() {
        return packets;
    }

    /**
     * Register a new packet for player's data.
     *
     * @param type   Packet type
     * @param packet Packet
     */
    public void registerPacket(PacketType type, Packet packet) {
        this.packets.put(type, packet);
    }

}