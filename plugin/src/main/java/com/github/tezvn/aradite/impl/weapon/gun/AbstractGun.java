package com.github.tezvn.aradite.impl.weapon.gun;

import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.packet.PacketType;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.api.recoil.RecoilVert;
import com.github.tezvn.aradite.api.weapon.BodyPart;
import com.github.tezvn.aradite.api.weapon.gun.Gun;
import com.github.tezvn.aradite.api.weapon.gun.GunType;
import com.github.tezvn.aradite.api.weapon.gun.animation.ShootAnimation;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMeta;
import com.github.tezvn.aradite.api.weapon.gun.meta.GunMetaType;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.api.language.Placeholder;
import com.github.tezvn.aradite.api.recoil.ZoomRatio;
import com.github.tezvn.aradite.impl.event.BulletHitEvent;
import com.github.tezvn.aradite.impl.recoil.Recoil;
import com.github.tezvn.aradite.impl.util.LocationUtils;
import com.github.tezvn.aradite.impl.weapon.gun.animation.RifleAndSMGAnimationImpl;
import com.github.tezvn.aradite.impl.weapon.gun.animation.ShotgunAnimationImpl;
import com.github.tezvn.aradite.impl.weapon.gun.animation.SniperAnimationImpl;
import com.github.tezvn.aradite.impl.weapon.gun.meta.AbstractGunMeta;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pdx.mantlecore.java.IntRange;
import pdx.mantlecore.task.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractGun implements Gun {

    private String ID;
    private Material material;
    private GunType type;
    private final GunMeta meta = new AbstractGunMeta();
    private String displayName;
    private boolean scopable;
    private ZoomRatio scopingMode;
    private List<String> lore;

    public AbstractGun(String ID, String displayname, GunType type) {
        this.ID = ID;
        this.type = type;
        this.displayName = displayname;
        this.material = Material.IRON_HOE;
    }

    @Override
    public ZoomRatio getScopeMode() {
        return scopingMode;
    }

    @Override
    public boolean isScopable() {
        return scopable;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public GunType getType() {
        return this.type;
    }

    @Override
    public GunMeta getMeta() {
        return this.meta;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setScopable(boolean scopable) {
        this.scopable = scopable;
    }

    public void setScopeMode(ZoomRatio scopingMode) {
        this.scopingMode = scopingMode;
    }

    @Override
    public List<String> getLore() {
        if (lore != null) return lore;

        Language lang = AraditeImpl.getInstance().getLanguage();

        GunMeta meta = getMeta();

        List<String> damageLore = new ArrayList<>();
        for (IntRange range : meta.getDamageTable().rowMap().keySet()) {
            Map<BodyPart, Integer> bulletToKills = meta.getDamageTable().rowMap().get(range);

            List<String> rangeDmg = lang.getListWithPlaceholders("lore.damage_range",
                    Placeholder.of("min_range", "" + range.getMin()),
                    Placeholder.of("max_range", "" + range.getMax()),
                    Placeholder.of("bullet_to_kill_head", "" + bulletToKills.get(BodyPart.HEAD)),
                    Placeholder.of("bullet_to_kill_body", "" + bulletToKills.get(BodyPart.BODY)),
                    Placeholder.of("bullet_to_kill_feet", "" + bulletToKills.get(BodyPart.LEGS))
            );
            damageLore.addAll(rangeDmg);
            damageLore.add("");
        }

        Map<GunMetaType, Double> attrs = meta.getAttribute();

        List<String> lore = lang.getListWithPlaceholders("lore.gun",
                Placeholder.of("wall_penetration", lang.getString("gun-meta-type.WALL_PENETRATION")),
                Placeholder.of("fire_rate", lang.getString("gun-meta-type.FIRE_RATE")),
                Placeholder.of("reload_speed", lang.getString("gun-meta-type.RELOAD_SPEED")),
                Placeholder.of("magazine", lang.getString("gun-meta-type.MAGAZINE")),
                Placeholder.of("reserve", lang.getString("gun-meta-type.RESERVE")),

                Placeholder.of("wall_penetration_value", "" + attrs.get(GunMetaType.WALL_PENETRATION)),
                Placeholder.of("fire_rate_value", "" + attrs.get(GunMetaType.FIRE_RATE)),
                Placeholder.of("reload_speed_value", "" + attrs.get(GunMetaType.RELOAD_SPEED)),
                Placeholder.of("magazine_value", "" + attrs.get(GunMetaType.MAGAZINE)),
                Placeholder.of("reserve_value", "" + attrs.get(GunMetaType.RESERVE)),

                Placeholder.of("damage_range", damageLore)
        );
        this.lore = lore;
        return getLore();
    }

    /**
     * Defines shoot particles or shoot algorithm for the gun
     *
     * @param player Player who shot.
     */
    public void shoot(Match match, Player player, boolean isInScopingMode) {
        ShootAnimation shootAnimation = null;
        switch (getType().getShootMode()) {
            case SNIPER:
            case RIFLE_SNIPER:
                shootAnimation = new SniperAnimationImpl();
                break;
            case RIFLE_AND_SMG:
                shootAnimation = new RifleAndSMGAnimationImpl();
                break;
            case SHOTGUN:
                shootAnimation = new ShotgunAnimationImpl();
                break;
        }
        if(shootAnimation == null)
            return;

        List<Location> bulletLines = shootAnimation.calculate(player, getType().getRange());

        Snowball bullet = player.launchProjectile(Snowball.class);
        bullet.setVelocity(player.getLocation().getDirection().multiply(4.1)); //4.5 originally
        bullet.setShooter(player);
        bullet.setGravity(false);
        bullet.setMetadata("bullet", new FixedMetadataValue(AraditeImpl.getInstance(), "bullet"));

        Gun gun = this;
        new BukkitRunnable() {
            final Vector velocity = bullet.getVelocity();

            Player target = null;
            BodyPart hitPart = null;

            @Override
            public void run() {
                if(bullet.isDead() || !bullet.isValid()){
                    this.cancel();
                    return;
                }

                bullet.setVelocity(velocity);

                Location loc = bullet.getLocation();
                Player entity = LocationUtils.getNearbyPlayers(loc, 0.5).stream().filter(en -> {
                            boolean condition1 = !en.equals(player);
                            return condition1 && (match == null || !match.getMatchTeam().isOnSameTeam(player, en));
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

                int bulletToKill = getMeta().getBulletsToKill(
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

                TaskQueue.runSync(AraditeImpl.getInstance(), () ->
                {
                    Bukkit.getPluginManager().callEvent(bulletHitEvent);

                    damage(match, player, target, bulletHitEvent);
                    bullet.remove();
                });

            }
        }.runTaskTimerAsynchronously(AraditeImpl.getInstance(), 0, 1);

        Recoil.sendRecoilEffect(player, RecoilVert.UPWARD, 5f);
    }

    @Override
    public void damage(Match match, Player dmger, LivingEntity target, Event e) {
    }

}
