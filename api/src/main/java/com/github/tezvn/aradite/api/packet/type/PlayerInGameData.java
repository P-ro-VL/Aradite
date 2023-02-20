package com.github.tezvn.aradite.api.packet.type;

import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketType;

import java.util.Map;

public interface PlayerInGameData {

    /**
     * Return the packet data whose type is {@code type}.
     *
     * @param type Packet type
     */
    Packet<?> getPacket(PacketType type);

    /**
     * Return the packet data whose class is {@code packetClass}
     * @param packetClass The class of the packet
     * @param <T> The packet class instance
     */
    <T extends Packet<?>> T getPacket(Class<T> packetClass);

    /**
     * Return all player's packet datas.
     */
    Map<PacketType, Packet<?>> getPackets();

    /**
     * Register a new packet for player's data.
     *
     * @param type   Packet type
     * @param packet Packet
     */
    void registerPacket(PacketType type, Packet<?> packet);
    
}
