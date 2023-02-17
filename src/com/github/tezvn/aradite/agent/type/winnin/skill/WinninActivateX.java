package com.github.tezvn.aradite.agent.type.winnin.skill;

import com.github.tezvn.aradite.agent.skill.Skill;
import com.github.tezvn.aradite.agent.skill.SkillType;
import com.github.tezvn.aradite.match.Match;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.Agents;
import com.github.tezvn.aradite.data.packet.PacketType;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.task.MatchTask;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.math.PrimaryMath;

public class WinninActivateX extends Skill {

    private final ItemStack BOMB_BALL_TEXTURE = new ItemBuilder(Material.PLAYER_HEAD).setTextureURL("").create();
    private final int DAMAGE_RADIUS = 3, MAX_DISTANCE = 20;

    public WinninActivateX() {
        super("winning-activate-x",
                Aradite.getInstance().getLanguage().getString("agents.winnin.skill.x.name"), Agents.WINNIN);

        setEvalExpression("damage", "(level/100)*175");
    }

    @Override
    public String getDescription() {
        return Aradite.getInstance().getLanguage().getString("agents.winnin.skill.x.description")
                .replaceAll("%range%", "" + DAMAGE_RADIUS);
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVE_X;
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        double dmg = PrimaryMath.eval(getSkillEvalExpressions().get("damage").replaceAll("level", "" + level));

        Vector eyeLocation = agent.getLocation().getDirection();
        eyeLocation.setY(0);
        final Vector direction = eyeLocation.normalize();

        final Location standingLocation = agent.getLocation().clone();
        ArmorStand ball = standingLocation.getWorld().spawn(standingLocation.clone().add(0, -0.95, 0),
                ArmorStand.class);
        ball.getEquipment().setHelmet(new ItemStack(Material.TNT));
        ball.addDisabledSlots(EquipmentSlot.HEAD);
        ball.setInvulnerable(true);
        ball.setVisible(false);
        ball.setGravity(false);
        ball.setMetadata("match-entity", new FixedMetadataValue(Aradite.getInstance(), "match-entity"));

        Location location = agent.getLocation();
        Vector hologramVector = location.clone().getDirection().normalize().multiply(3);
        Location hologramLocation = location.clone().add(hologramVector.getX(), 0, hologramVector.getZ());
        final Vector vector = hologramLocation.toVector().subtract(location.toVector()).normalize();

        ball.getWorld().playSound(ball.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
        BukkitTask task = new BukkitRunnable() {
            int angle = 0;

            @Override
            public void run() {
                angle += 10;
                if (ball.getLocation().getBlock().getType().isAir()) {
                    Block block = ball.getLocation().getWorld().getHighestBlockAt(ball.getLocation());
                    ball.teleportAsync(ball.getLocation().clone().add(0,
                            -(ball.getLocation().getY() - block.getY()) + 0.05, 0));
                }

                Location ballLocation = ball.getLocation().clone().add(0, 1.25, 0);
                Block block = ballLocation.getBlock();

                Player enemy = ballLocation.getNearbyPlayers(1).stream().filter(player ->
                        !match.getMatchTeam().isOnSameTeam(agent, player)).findAny().orElse(null);
                if (enemy != null) {
                    boom(ballLocation);
                    return;
                }

                if (standingLocation.distance(ballLocation) > MAX_DISTANCE || block.getType().isSolid()
                        || (ball == null || !ball.isValid() || ball.isDead())) {
                    boom(ballLocation);
                    return;
                }

                ball.setHeadPose(new EulerAngle(0, Math.toRadians(angle = angle > 360 ? 1 : angle),
                        Math.toRadians(270)));
                ball.teleportAsync(ball.getLocation().add(vector.getX() / 2, 0, vector.getZ() / 2));
            }

            /**
             * Boom the ball
             *
             * @param location
             *            The explode location
             */
            public void boom(Location location) {
                this.cancel();
                ball.remove();

                location.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, location, 2, 0.5, 0.5, 0.5, 0.5);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5, 0);

                location.getNearbyPlayers(DAMAGE_RADIUS, player -> {
                    return !match.getMatchTeam().isOnSameTeam(player, agent);
                }).forEach(player -> {
                    PlayerInGameAttributePacket targetData = (PlayerInGameAttributePacket) match
                            .retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                    PlayerInGameLastDamagePacket targetLastDmgData = (PlayerInGameLastDamagePacket) match
                            .retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);

                    targetData.damage("SKILL:" + agent.getName() + "•Winnin•ACTIVE_X", dmg, false,
                            targetLastDmgData);
                });
            }
        }.runTaskTimer(Aradite.getInstance(), 1, 1);

        MatchTask taskManager = match.getMatchTask();
        taskManager.addSimpleTask(task);
    }

}
