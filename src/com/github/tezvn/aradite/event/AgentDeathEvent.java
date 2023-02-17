package com.github.tezvn.aradite.event;

import com.github.tezvn.aradite.data.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.match.Match;
import com.github.tezvn.aradite.weapon.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event that will be called when an agent dies in the match.
 */
public class AgentDeathEvent extends Event implements Cancellable {

    private final Match match;
    private final Player killer;
    private final Player target;
    private final PlayerInGameLastDamagePacket.DeathReason deathReason;
    private final Weapon weapon;
    private final String skill;

    public AgentDeathEvent(Match match, Player killer, Player target, PlayerInGameLastDamagePacket.DeathReason deathReason, Weapon weapon, String skill) {
        super(true);
        this.match = match;
        this.killer = killer;
        this.target = target;
        this.deathReason = deathReason;
        this.weapon = weapon;
        this.skill = skill;
    }

    /**
     * Return the name of the skill killed player.
     */
    public String getSkill() {
        return skill;
    }

    /**
     * Return the reason why player died.
     */
    public PlayerInGameLastDamagePacket.DeathReason getDeathReason() {
        return deathReason;
    }

    /**
     * Return the weapon the killer used to kill player.
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * Return the match that killer and player is in.
     * @return
     */
    public Match getMatch() {
        return match;
    }

    /**
     * Return the killer.
     */
    public Player getKiller() {
        return killer;
    }

    /**
     * Return the player instance.
     * @return
     */
    public Player getTarget() {
        return target;
    }

    private static final HandlerList handlers = new HandlerList();

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
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
