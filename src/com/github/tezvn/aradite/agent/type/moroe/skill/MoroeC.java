package com.github.tezvn.aradite.agent.type.moroe.skill;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.Agents;
import com.github.tezvn.aradite.agent.attribute.AttributeType;
import com.github.tezvn.aradite.agent.skill.Skill;
import com.github.tezvn.aradite.agent.skill.SkillType;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.Match;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;
import pdx.mantlecore.math.DimensionalMath;
import pdx.mantlecore.math.Shapes;

public class MoroeC extends Skill {

    private static final int DAMAGE_REDUCE = 30, DURATION = 3;

    private static Language lang = Aradite.getInstance().getLanguage();

    public MoroeC() {
        super("moroe-c", lang.getString("agents.moroe.skill.c.name"), Agents.MOROE);
    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        PlayerInGameAttributePacket attributePacket = match.retrieveProtocol(agent)
                .getPacket(PlayerInGameAttributePacket.class);
        attributePacket.setAttribute(AttributeType.DAMAGE_REDUCE_IN_PERCENT, DAMAGE_REDUCE);

        new BukkitRunnable() {
            int second = 0;

            @Override
            public void run() {
                second++;
                if (second >= DURATION) {
                    this.cancel();
                    attributePacket.setAttribute(AttributeType.DAMAGE_REDUCE_IN_PERCENT, 0);
                    return;
                }

                Location location = DimensionalMath.getCentral(agent.getLocation(), agent.getEyeLocation());
                for (Location loc : Shapes.sphere(location, 2, true)) {
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.LIME, 1));
                }
            }
        }.runTaskTimerAsynchronously(Aradite.getInstance(), 20, 20);
    }

    @Override
    public String getDescription() {
        return lang.getString("agents.moroe.skill.c.description")
                .replaceAll("%damage_reduce%", "" + DAMAGE_REDUCE)
                .replaceAll("%duration%", "" + DURATION);
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVE_C;
    }
}
