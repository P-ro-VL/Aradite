package com.github.tezvn.aradite.impl.agent.type.moroe.skill;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.agent.skill.SkillImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameAttributePacket;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pdx.mantlecore.math.DimensionalMath;
import pdx.mantlecore.math.Shapes;

public class MoroeC extends SkillImpl {

    private static final int DAMAGE_REDUCE = 30, DURATION = 3;

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
        }.runTaskTimerAsynchronously(AraditeImpl.getInstance(), 20, 20);
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
