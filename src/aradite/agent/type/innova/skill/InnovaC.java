package aradite.agent.type.innova.skill;

import aradite.Aradite;
import aradite.agent.Agents;
import aradite.agent.skill.Skill;
import aradite.agent.skill.SkillType;
import aradite.language.Language;
import aradite.match.Match;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftTippedArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class InnovaC extends Skill {

    private static final int DETECT_ENEMY_RANGE = 4, LEVITATION_DURATION = 3;

    public InnovaC() {
        super("innova-c", lang.getString("agents.innova.skill.c.name"), Agents.INNOVA);

        registerListener(new Listener() {
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
        bullet.setMetadata("innova-c", new FixedMetadataValue(Aradite.getInstance(), "innova-c"));
        new BukkitRunnable() {
            private Player target = null;

            @Override
            public void run() {
                if (bullet.isDead() || !bullet.isValid() || bullet.isOnGround()) {
                    this.cancel();
                    return;
                }

                target = (Player) bullet.getLocation().getNearbyPlayers(DETECT_ENEMY_RANGE).stream().filter(
                        player -> !match.getMatchTeam().isOnSameTeam(player, agent)
                ).findAny().orElse(null);

                if (target == null) return;

                Vector bulletVector = bullet.getLocation().toVector();
                Vector targetVector = target.getLocation().toVector();

                Vector velocity = targetVector.subtract(bulletVector);
                bullet.setVelocity(velocity);

                bullet.getWorld().spawnParticle(Particle.WATER_BUBBLE, bullet.getLocation(), 2);
            }
        }.runTaskTimerAsynchronously(Aradite.getInstance(), 1, 1);
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
