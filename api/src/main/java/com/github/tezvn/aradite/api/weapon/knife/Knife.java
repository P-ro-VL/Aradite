package com.github.tezvn.aradite.api.weapon.knife;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.packet.PacketType;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.api.weapon.WeaponCategory;
import com.github.tezvn.aradite.api.weapon.WeaponType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface Knife extends Weapon {

    @Override
    default WeaponType getWeaponType() {
        return WeaponType.SUB;
    }

    @Override
    default boolean isMelee() {
        return true;
    }

    @Override
    default WeaponCategory getCategory() {
        return WeaponCategory.KNIFE;
    }

}
