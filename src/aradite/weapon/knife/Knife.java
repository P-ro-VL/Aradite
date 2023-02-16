package aradite.weapon.knife;

import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameAttributePacket;
import aradite.data.packet.type.PlayerInGameLastDamagePacket;
import aradite.match.Match;
import aradite.weapon.Weapon;
import aradite.weapon.WeaponCategory;
import aradite.weapon.WeaponType;
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

    @Override
    default void onDamage(Match match, Player dmger, LivingEntity target, Event event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        if (target instanceof Player) {
            Player entity = (Player) target;
            if (match != null) {
                PlayerInGameAttributePacket packet = (PlayerInGameAttributePacket) match.retrieveProtocol(entity)
                        .getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                PlayerInGameLastDamagePacket lastDamagePacket = (PlayerInGameLastDamagePacket) match.retrieveProtocol(entity)
                        .getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);
                packet.damage(dmger.getName() + "•" + getID(), e.getDamage(), true, lastDamagePacket);
            } else {
                entity.damage(e.getDamage());
            }
        }
    }
}
