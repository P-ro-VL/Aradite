package aradite.weapon.gun;

import aradite.Aradite;
import aradite.agent.attribute.AttributeType;
import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameAttributePacket;
import aradite.data.packet.type.PlayerInGameLastDamagePacket;
import aradite.event.BulletHitEvent;
import aradite.match.Match;
import aradite.nms.recoil.Recoil;
import aradite.nms.recoil.RecoilVert;
import aradite.nms.recoil.ZoomRatio;
import aradite.task.MatchTask;
import aradite.team.MatchTeam;
import aradite.weapon.*;
import aradite.weapon.gun.meta.GunMeta;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import pdx.mantlecore.math.Shapes;
import pdx.mantlecore.task.TaskQueue;

import java.util.ArrayList;
import java.util.List;

public interface Gun extends Weapon {

    static final NamespacedKey ID_KEY = new NamespacedKey(Aradite.getInstance(), "weapon_id");

    /**
     * Return the type of the gun.
     */
    public GunType getType();

    /**
     * Return {@code true} if this gun has a scoping mode.
     */
    public boolean isScopable();

    /**
     * Return the zoom ratio of the gun if this gun is {@link #isScopable() scopable}.
     */
    public ZoomRatio getScopeMode();

    @Override
    default boolean isMelee() {
        return false;
    }

    @Override
    default WeaponType getWeaponType() {
        return WeaponType.MAIN;
    }

    @Override
    default void onDamage(Match match, Player dmger, LivingEntity target, Event event) {
        BulletHitEvent e = (BulletHitEvent) event;

        if (target instanceof Player) {
            Player entity = (Player) target;
            if (match != null) {
                PlayerInGameAttributePacket packet = (PlayerInGameAttributePacket) match.retrieveProtocol(entity)
                        .getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                PlayerInGameLastDamagePacket lastDamagePacket = (PlayerInGameLastDamagePacket) match.retrieveProtocol(entity)
                        .getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);
                packet.damage("GUN:" + dmger.getName() + "•" + getID(), e.getDamage(), true, lastDamagePacket);
            } else {
                entity.damage(e.getDamage());
            }
        }
    }

    @Override
    default WeaponCategory getCategory() {
        return WeaponCategory.GUN;
    }

    /**
     * Defines shoot particles or shoot algorithm for the gun
     *
     * @param player Player who shot.
     */
    default void onShoot(Match match, Player player, boolean isInScopingMode) {
        List<Location> bulletLines = getType().getShootMode().getAnimation().calculate(player, getType().getRange());

        Snowball bullet = player.launchProjectile(Snowball.class);
        bullet.setVelocity(player.getLocation().getDirection().multiply(4.1)); //4.5 originally
        bullet.setShooter(player);
        bullet.setGravity(false);
        bullet.setMetadata("bullet", new FixedMetadataValue(Aradite.getInstance(), "bullet"));

        Gun gun = this;
        new BukkitRunnable() {
            final Vector velocity = bullet.getVelocity();

            Player target = null;
            BodyPart hitPart = null;

            @Override
            public void run() {
                if(bullet == null || bullet.isDead() || !bullet.isValid()){
                    this.cancel();
                    return;
                }

                bullet.setVelocity(velocity);

                Location loc = bullet.getLocation();
                Player entity = loc.getNearbyPlayers(0.5).stream().filter(en -> {
                            boolean condition1 = !en.equals(player);
                            return condition1 && (match == null ? true : !match.getMatchTeam().isOnSameTeam(player, en));
                        })
                        .findAny().orElse(null);
                if (entity != null) {
                    this.target = entity;

                    Location entityLocation = entity.getLocation();
                    Location projectileLocation = loc;

                    double deltaY = projectileLocation.getY() - entityLocation.getY();

                    if (deltaY > 1.35) {
                        hitPart = BodyPart.HEAD;
                        return;
                    }

                    if (deltaY <= 0.7) {
                        hitPart = BodyPart.LEGS;
                        return;
                    }

                    Vector vtcp = entityLocation.clone().toVector()
                            .subtract(entityLocation.clone().add(0, player.getHeight() + 0.5, 0).toVector());
                    Vector supportVector = entityLocation.toVector().subtract(projectileLocation.toVector());

                    double distance = (supportVector.crossProduct(vtcp).length()) / vtcp.length();
                    if (distance > 0.3) {
                        hitPart = BodyPart.HANDS;
                    } else {
                        hitPart = BodyPart.BODY;
                    }
                    return;
                }

                if (hitPart == null) {
                    return;
                }

                int bulletToKill = ((GunMeta) getMeta()).getBulletsToKill(
                        (int) player.getLocation().distance(target.getLocation()), hitPart);

                double health = 0d;

                if (match != null) {
                    PlayerInGameAttributePacket entityHealth = (PlayerInGameAttributePacket)
                            match.retrieveProtocol(target).getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                    health = entityHealth.getAttribute(AttributeType.MAX_HEALTH);
                } else {
                    health = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                }

                double damage = health / ((double) bulletToKill);

                BulletHitEvent bulletHitEvent = new BulletHitEvent(player, target, gun, damage, hitPart);

                TaskQueue.runSync(Aradite.getInstance(), () ->
                {
                    Bukkit.getPluginManager().callEvent(bulletHitEvent);

                    onDamage(match, player, target, bulletHitEvent);
                    bullet.remove();
                });

            }
        }.runTaskTimerAsynchronously(Aradite.getInstance(), 0, 1);

        Recoil.sendRecoilEffect(player, RecoilVert.UPWARD, 5f);
    }

}
