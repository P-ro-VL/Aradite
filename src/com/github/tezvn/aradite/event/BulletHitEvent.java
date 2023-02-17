package com.github.tezvn.aradite.event;

import com.github.tezvn.aradite.weapon.BodyPart;
import com.github.tezvn.aradite.weapon.gun.Gun;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event called when the bullets from gun weapon hit an entity.
 */
public class BulletHitEvent extends Event implements Cancellable  {

    private Player shooter;
    private LivingEntity target;
    private Gun gun;
    private double damage;
    private BodyPart hitPart;

    public BulletHitEvent(Player shooter, LivingEntity target, Gun gun, double damage, BodyPart hitPart) {
        this.shooter = shooter;
        this.target = target;
        this.gun = gun;
        this.damage = damage;
        this.hitPart = hitPart;
    }

    /**
     * Return the player who shot the bullets.
     */
    public Player getShooter() {
        return shooter;
    }

    /**
     * Return the {@link LivingEntity} that hit by the bullets.
     */
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * The gun shooter used to shoot.
     */
    public Gun getGun() {
        return gun;
    }

    /**
     * The damage the bullets has dealt.
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Change the damage the bullets will deal.
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Return the part of target's body hit by the bullets.
     */
    public BodyPart getHitPart() {
        return hitPart;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
