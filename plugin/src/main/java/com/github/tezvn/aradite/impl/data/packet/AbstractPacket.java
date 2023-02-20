package com.github.tezvn.aradite.impl.data.packet;

import com.github.tezvn.aradite.api.packet.Packet;
import com.github.tezvn.aradite.api.packet.PacketPackage;
import org.bukkit.entity.Player;

public abstract class AbstractPacket<T> implements Packet<T> {

    private final Player player;

    private final PacketPackage<T> packet = new DefaultPacketPackage<>();

    public AbstractPacket(Player player) {
        this.player = player;
    }

    @Override
    public Player getPacketOwner() {
        return this.player;
    }

    @Override
    public PacketPackage<T> getPacketContents() {
        return this.packet;
    }

}
