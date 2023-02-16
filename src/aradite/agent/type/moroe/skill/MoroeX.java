package aradite.agent.type.moroe.skill;

import aradite.Aradite;
import aradite.agent.Agents;
import aradite.agent.skill.Skill;
import aradite.agent.skill.SkillType;
import aradite.data.packet.type.PlayerInGameAttributePacket;
import aradite.data.packet.type.PlayerInGameLastDamagePacket;
import aradite.language.Language;
import aradite.match.Match;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MoroeX extends Skill {

    private static final int EMPHASIZE_DELAY = 3, LINE_LENGTH = 10, DAMAGE = 10, SLOW_DURATION = 3;
    private static Language lang = Aradite.getInstance().getLanguage();

    public MoroeX() {
        super("moroe-x", lang.getString("agents.moroe.skill.x.name"), Agents.MOROE);
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        Collection<Location> lines = Shapes.lineFromPlayer(agent, LINE_LENGTH, 0.5);
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (tick / 20 >= EMPHASIZE_DELAY) {
                    lines.forEach(loc -> {
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 3, new Particle.DustOptions(Color.GREEN
                                , 10));
                        loc.getNearbyPlayers(0.5).stream().filter(player ->
                                !match.getMatchTeam().isOnSameTeam(agent, player)).forEach(player -> {
                            PlayerInGameAttributePacket attributePacket = match.retrieveProtocol(player)
                                    .getPacket(PlayerInGameAttributePacket.class);
                            attributePacket.damage("SKILL:" + agent.getName() + "•Moroe•ACTIVE_X", DAMAGE, true,
                                    match.retrieveProtocol(player).getPacket(PlayerInGameLastDamagePacket.class));

                            TaskQueue.runSync(Aradite.getInstance(), () -> {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SLOW_DURATION * 20,
                                        1, false, false, false));
                            });
                        });
                    });
                    agent.getWorld().playSound(agent.getLocation(), Sound.ITEM_TRIDENT_THUNDER, LINE_LENGTH, 1);
                    this.cancel();
                    return;
                }

                lines.forEach(loc -> loc.getWorld().spawnParticle(Particle.REDSTONE, loc,
                        1, new Particle.DustOptions(Color.LIME, 1)));
            }
        }.runTaskTimerAsynchronously(Aradite.getInstance(), 1, 1);
    }

    @Override
    public String getDescription() {
        return lang.getString("agents.moroe.skill.x.description")
                .replaceAll("%delay%", "" + EMPHASIZE_DELAY)
                .replaceAll("%slow_duration%", "" + SLOW_DURATION);
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVE_X;
    }
}
