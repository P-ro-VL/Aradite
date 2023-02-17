package com.github.tezvn.aradite.impl.agent.type.innova.skill;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.agent.skill.SkillImpl;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public class InnovaC extends SkillImpl {

    private static final int DETECT_ENEMY_RANGE = 4, LEVITATION_DURATION = 3;

    public InnovaC() {
        super("innova-c", lang.getString("agents.innova.skill.c.name"), Agents.INNOVA);

        registerListener(AraditeImpl.getInstance(), new Listener() {
            @EventHandler
            public void onHit(ProjectileHitEvent e) {
                Projectile projectile = e.getEntity();
                if (projectile.hasMetadata("innova-c")) {
                    e.setCancelled(true);
                    projectile.remove();
                }
            }
        });
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        Arrow bullet = agent.launchProjectile(Arrow.class);
        bullet.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, LEVITATION_DURATION * 20, 1,
                false, false, false), false);
        bullet.setMetadata("innova-c", new FixedMetadataValue( AraditeImpl.getInstance(), "innova-c"));
        new BukkitRunnable() {
            private Player target = null;

            @Override
            public void run() {
                if (bullet.isDead() || !bullet.isValid() || bullet.isOnGround()) {
                    this.cancel();
                    return;
                }

                target = getNearestEnemy(bullet.getLocation(), match, agent, DETECT_ENEMY_RANGE);

                if (target == null) return;

                Vector bulletVector = bullet.getLocation().toVector();
                Vector targetVector = target.getLocation().toVector();

                Vector velocity = targetVector.subtract(bulletVector);
                bullet.setVelocity(velocity);

                bullet.getWorld().spawnParticle(Particle.WATER_BUBBLE, bullet.getLocation(), 2);
            }
        }.runTaskTimerAsynchronously( AraditeImpl.getInstance(), 1, 1);
    }

    @Override
    public String getDescription() {
        return lang.getString("agents.innova.skill.c.description")
                .replaceAll("%detect-range%", "" + DETECT_ENEMY_RANGE)
                .replaceAll("%levitation-duration%", "" + LEVITATION_DURATION);
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVE_C;
    }
}
