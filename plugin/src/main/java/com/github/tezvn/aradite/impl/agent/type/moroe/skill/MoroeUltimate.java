package com.github.tezvn.aradite.impl.agent.type.moroe.skill;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.agent.skill.UltimateSkillImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameAttributePacketImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameLastDamagePacketImpl;
import com.github.tezvn.aradite.impl.util.LocationUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pdx.mantlecore.math.Shapes;
import pdx.mantlecore.task.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoroeUltimate extends UltimateSkillImpl {

    private static final int MAX_RANGE = 8, STUN_DURATION = 3, MAX_ENEMY = 3, DAMAGE = 20, MAX_ELECTRIC_RAY_DAMAGE_TIMES = 3;

    public MoroeUltimate() {
        super("moroe-ultimate", lang.getString("agents.moroe.skill.ultimate.name"), Agents.MOROE);
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        new ElectricWaveAnimation(match, agent).runTaskTimerAsynchronously(AraditeImpl.getInstance(), 2, 2);
    }

    @Override
    public String getDescription() {
        return lang.getString("agents.moroe.skill.ultimate.description")
                .replaceAll("%max_range%", "" + MAX_RANGE)
                .replaceAll("%stun_duration%", "" + STUN_DURATION)
                .replaceAll("%max_enemy%", "" + MAX_ENEMY);
    }

    @Override
    public SkillType getType() {
        return SkillType.ULTIMATE;
    }

    public static class SpreadElectricRayAnimation extends BukkitRunnable {

        private final Player agent;
        private final Match match;

        private List<Player> spreadPlayers = new ArrayList<>();

        public SpreadElectricRayAnimation(Match match, Player agent) {
            this.agent = agent;
            this.match = match;

            this.spreadPlayers.add(agent);
            this.spreadPlayers.addAll(LocationUtils.getNearbyPlayers(agent.getLocation(), MAX_RANGE, MAX_ENEMY));
        }

        int second = 0;

        @Override
        public void run() {
            second++;
            if (second == MAX_ELECTRIC_RAY_DAMAGE_TIMES) {
                this.cancel();
                return;
            }

            for (int i = 0; i < spreadPlayers.size() - 1; i++) {
                Player player1 = spreadPlayers.get(i);
                Player player2 = spreadPlayers.get(i + 1);

                Location loc1 = player1.getEyeLocation();
                Location loc2 = player2.getEyeLocation();

                for (Location loc : Shapes.line(loc1, loc2, 0.4)) {
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.GREEN, 2));
                }

                player1.playSound(player1.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1, 1);
                player2.playSound(player2.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1, 1);

                if (!player1.equals(agent)) {
                    PlayerInGameAttributePacketImpl player1AttrPacket = match.retrieveProtocol(player1)
                            .getPacket(PlayerInGameAttributePacketImpl.class);
                    player1AttrPacket.damage("SKILL:" + agent.getName() + "???Moroe???ULTIMATE", DAMAGE, true, match.retrieveProtocol(player1)
                            .getPacket(PlayerInGameLastDamagePacketImpl.class));
                }

                if (i + 1 == this.spreadPlayers.size() - 1 && !player2.equals(agent)) {
                    PlayerInGameAttributePacketImpl player2AttrPacket = match.retrieveProtocol(player2)
                            .getPacket(PlayerInGameAttributePacketImpl.class);
                    player2AttrPacket.damage("SKILL:" + agent.getName() + "???Moroe???ULTIMATE", DAMAGE, true, match.retrieveProtocol(player2)
                            .getPacket(PlayerInGameLastDamagePacketImpl.class));
                }

            }
        }
    }

    public static class ElectricWaveAnimation extends BukkitRunnable {

        private final Match match;
        private final Player agent;

        public ElectricWaveAnimation(Match match, Player agent) {
            this.match = match;
            this.agent = agent;
        }

        int range = 0;

        @Override
        public void run() {
            range++;
            if (range >= MAX_RANGE) {
                TaskQueue.runSync(AraditeImpl.getInstance(), () -> {
                    LocationUtils.getNearbyPlayers(agent.getLocation(), MAX_RANGE)
                            .stream().filter(player -> !match.getMatchTeam().isOnSameTeam(agent, player))
                            .forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, STUN_DURATION * 20,
                                    255, false, false, false)));
                });
                this.cancel();
                agent.getWorld().playSound(agent.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
                new SpreadElectricRayAnimation(match, agent).runTaskTimerAsynchronously(AraditeImpl.getInstance(),
                        20, 20);
                return;
            }

            Shapes.circle(agent.getLocation().clone().add(0, 0.3, 0), range, 50).forEach(loc -> {
                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.LIME, 2));
            });
        }
    }

}
