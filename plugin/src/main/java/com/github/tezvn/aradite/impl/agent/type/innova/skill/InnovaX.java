package com.github.tezvn.aradite.impl.agent.type.innova.skill;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.agent.skill.SkillImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameAttributePacketImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameLastDamagePacketImpl;
import com.github.tezvn.aradite.impl.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.math.DimensionalMath;
import pdx.mantlecore.math.Shapes;
import pdx.mantlecore.task.TaskQueue;

import java.util.Arrays;

public class InnovaX extends SkillImpl {

    private static final int TURRET_ACTIVATION_TIME = 8, ACTIVATION_RANGE = 8, MAX_BOMB_AMOUNT = 10, DAMAGE = 6;

    public InnovaX() {
        super("innova-x", lang.getString("agents.innova.skill.x.name"), Agents.INNOVA);
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        final Location agentLocation = agent.getLocation();
        new BukkitRunnable() {

            private ArmorStand turret = null;
            private int tick = 0;

            @Override
            public void run() {
                if(turret == null) {
                    init();
                    return;
                }

                tick++;
                int seconds = tick / 20;
                if (seconds >= TURRET_ACTIVATION_TIME || !turret.isValid() || turret.isDead()) {
                    this.cancel();
                    turret.remove();
                    return;
                }

                if (seconds == TURRET_ACTIVATION_TIME - 1) {
                    Shapes.circle(turret.getLocation(), ACTIVATION_RANGE, MAX_BOMB_AMOUNT).forEach(
                            this::shootToPosition
                    );
                    cancel();
                    turret.remove();
                    return;
                }

                Player nearestEnemy = getNearestEnemy(turret.getLocation(), match, agent, ACTIVATION_RANGE);
                if (nearestEnemy == null) return;
                shootToPosition(nearestEnemy.getLocation());
            }

            public void shootToPosition(Location targetLocation) {
                Vector parabolaVector = DimensionalMath.createParabola(turret.getLocation(), targetLocation, 3);
                SplashPotion splashPotion = targetLocation.getWorld()
                        .spawn(turret.getLocation().clone().add(0,1,0), SplashPotion.class);
                splashPotion.setVelocity(parabolaVector);

                Player enemy = LocationUtils.getNearbyPlayers(targetLocation, 2).stream()
                        .filter(player -> !match.getMatchTeam().isOnSameTeam(agent, player)).findAny().orElse(null);
                if(enemy != null){
                    PlayerInGameAttributePacketImpl attributePacket = match.retrieveProtocol(enemy)
                            .getPacket(PlayerInGameAttributePacketImpl.class);
                    attributePacket.damage("SKILL:" + agent.getName() + "•Innova•ACTIVE_X", DAMAGE, true,
                            match.retrieveProtocol(enemy).getPacket(PlayerInGameLastDamagePacketImpl.class));
                    enemy.getWorld().playSound(targetLocation, Sound.BLOCK_WATER_AMBIENT, 1, 1);
                    enemy.getWorld().playSound(targetLocation, Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 1);
                }
            }

            public BukkitRunnable init() {
                ArmorStand turret = agentLocation.getWorld().spawn(agentLocation.clone().add(0, -0.95, 0),
                        ArmorStand.class);
                turret.setVisible(false);
                Arrays.stream(EquipmentSlot.values()).forEach(slot ->
                        turret.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING));
                turret.setCustomName(match.getMatchTeam().getTeamColor(agent) + lang.getString("agents.innova.turret_name"));
                turret.setCustomNameVisible(true);

                ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                        .setTextureURL("211c537c4552e15f11cc353e3b40cd0dbc8fcc02a3e286e325f4216b45ff9df0").create();
                turret.setHelmet(head);

                this.turret = turret;
                return this;
            }
        }.init().runTaskTimer(AraditeImpl.getInstance(), 20, 20);
    }

    @Override
    public String getDescription() {
        return lang.getString("agents.innova.skill.x.description")
                .replaceAll("%turret_activation_time%", "" + TURRET_ACTIVATION_TIME)
                .replaceAll("%activation_range%", "" + ACTIVATION_RANGE)
                .replaceAll("%max_bomb_amount%", "" + MAX_BOMB_AMOUNT);
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVE_X;
    }
}
