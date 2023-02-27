package com.github.tezvn.aradite.impl.agent.type.winnin.skill;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.task.MatchTask;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.agent.skill.SkillImpl;
import com.github.tezvn.aradite.api.packet.PacketType;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameAttributePacketImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameLastDamagePacketImpl;
import com.github.tezvn.aradite.impl.task.MatchTaskImpl;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.math.PrimaryMath;

import java.util.Objects;

public class WinninActivateX extends SkillImpl {

    private final ItemStack BOMB_BALL_TEXTURE = new ItemBuilder(Material.PLAYER_HEAD).setTextureURL("").create();
    private final int DAMAGE_RADIUS = 3, MAX_DISTANCE = 20;

    public WinninActivateX() {
        super("winning-activate-x",
                 AraditeImpl.getInstance().getLanguage().getString("agents.winnin.skill.x.name"), Agents.WINNIN);

        setEvalExpression("damage", "(level/100)*175");
    }

    @Override
    public String getDescription() {
        return  AraditeImpl.getInstance().getLanguage().getString("agents.winnin.skill.x.description")
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
        EntityEquipment equipment = ball.getEquipment();
        if(equipment != null)
            equipment.setHelmet(new ItemStack(Material.TNT));
        ball.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        ball.setInvulnerable(true);
        ball.setVisible(false);
        ball.setGravity(false);
        ball.setMetadata("match-entity", new FixedMetadataValue( AraditeImpl.getInstance(), "match-entity"));

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
                    Block block = Objects.requireNonNull(ball.getLocation().getWorld())
                            .getHighestBlockAt(ball.getLocation());

                    ball.teleport(ball.getLocation().clone().add(0,
                            -(ball.getLocation().getY() - block.getY()) + 0.05, 0));
                }

                Location ballLocation = ball.getLocation().clone().add(0, 1.25, 0);
                Block block = ballLocation.getBlock();

                Player enemy = getNearestEnemy(ballLocation, match, agent, 1);
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
                ball.teleport(ball.getLocation().add(vector.getX() / 2, 0, vector.getZ() / 2));
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

                getNearbyPlayers(location, match, agent, DAMAGE_RADIUS).forEach(player -> {
                    PlayerInGameAttributePacketImpl targetData = (PlayerInGameAttributePacketImpl) match
                            .retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                    PlayerInGameLastDamagePacketImpl targetLastDmgData = (PlayerInGameLastDamagePacketImpl) match
                            .retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);

                    targetData.damage("SKILL:" + agent.getName() + "•Winnin•ACTIVE_X", dmg, false,
                            targetLastDmgData);
                });
            }
        }.runTaskTimer( AraditeImpl.getInstance(), 1, 1);

        MatchTask taskManager = match.getMatchTask();
        taskManager.addSimpleTask(task);
    }

}
