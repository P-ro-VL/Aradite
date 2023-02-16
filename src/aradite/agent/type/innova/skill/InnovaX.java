package aradite.agent.type.innova.skill;

import aradite.Aradite;
import aradite.agent.Agents;
import aradite.agent.skill.Skill;
import aradite.agent.skill.SkillType;
import aradite.match.Match;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import pdx.mantlecore.math.Shapes;
import pdx.mantlecore.task.TaskQueue;

import java.util.stream.Collectors;

public class InnovaX extends Skill {

    private static final int TURRET_ACTIVATION_TIME = 8, ACTIVATION_RANGE = 8, MAX_BOMB_AMOUNT = 10;

    public InnovaX() {
        super("innova-x", lang.getString("agents.innova.skill.x.name"), Agents.INNOVA);
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        final Location agentLocation = agent.getLocation();
        new BukkitRunnable() {

            private ArmorStand turret = null;
            private int tick = 0;
            private ThrownPotion bullet = null;

            @Override
            public void run() {
                tick++;
                int seconds = tick / 20;
                if (seconds >= TURRET_ACTIVATION_TIME || turret == null || !turret.isValid() || turret.isDead()) {
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

                Player nearestEnemy = turret.getLocation().getNearbyPlayers(ACTIVATION_RANGE).stream().filter(player ->
                        !match.getMatchTeam().isOnSameTeam(player, agent)).findAny().orElse(null);
                if (nearestEnemy == null) return;
                shootToPosition(nearestEnemy.getLocation());
            }

            public void shootToPosition(Location targetLocation) {

            }

            public BukkitRunnable init() {
                ArmorStand turret = agentLocation.getWorld().spawn(agentLocation.clone().add(0, -0.95, 0),
                        ArmorStand.class);
                turret.setVisible(false);
                turret.setDisabledSlots(EquipmentSlot.values());
                turret.setCustomName("");
                turret.setCustomNameVisible(true);

                this.turret = turret;
                return this;
            }
        }.init().runTaskTimerAsynchronously(Aradite.getInstance(), 20, 20);
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
