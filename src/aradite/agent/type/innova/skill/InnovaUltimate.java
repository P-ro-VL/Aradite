package aradite.agent.type.innova.skill;

import aradite.Aradite;
import aradite.agent.Agents;
import aradite.agent.attribute.AttributeType;
import aradite.agent.skill.Skill;
import aradite.agent.skill.SkillType;
import aradite.agent.skill.UltimateSkill;
import aradite.data.packet.type.PlayerInGameAttributePacket;
import aradite.match.Match;
import aradite.team.MatchTeam;
import aradite.team.Team;
import aradite.team.TeamRole;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.java.StringUtils;

public class InnovaUltimate extends UltimateSkill {

    private static final int GLOWING_DURATION = 5, ARMOR_REDUCE_PERCENT = 30;

    public InnovaUltimate() {
        super("innova-ultimate", lang.getString("agents.innova.skills.ultimate.name"), Agents.INNOVA);

    }

    @Override
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock) {
        MatchTeam matchTeam = match.getMatchTeam();
        TeamRole role = matchTeam.getPlayerTeam(agent);
        Team team = matchTeam.getTeam(role.not());
        agent.playSound(agent.getEyeLocation(), Sound.BLOCK_BELL_USE, 2, 1);
        agent.playSound(agent.getEyeLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 2, 1);
        team.getMembers().forEach(player -> {
            player.playSound(player.getEyeLocation(), Sound.AMBIENT_UNDERWATER_ENTER, 2, 1);
            player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, GLOWING_DURATION * 20, 1,
                    false, false, false), false);
            player.sendMessage(StringUtils.alignCenter(ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH,
                    lang.getString("effects.reveal")
                            .replaceAll("%second%", "" + GLOWING_DURATION)));
            player.sendMessage(StringUtils.alignCenter(ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH,
                    lang.getString("effects.armor-weaken")
                            .replaceAll("%reduce%", "" + ARMOR_REDUCE_PERCENT + "%")
                            .replaceAll("%second%", "" + GLOWING_DURATION)));

            PlayerInGameAttributePacket attributePacket = match.retrieveProtocol(player)
                    .getPacket(PlayerInGameAttributePacket.class);
            final double currentArmor = attributePacket.getAttribute(AttributeType.ARMOR);
            double changedArmor = (currentArmor / 100) * ARMOR_REDUCE_PERCENT;
            attributePacket.setAttribute(AttributeType.ARMOR, changedArmor);
            new BukkitRunnable() {
                @Override
                public void run() {
                    attributePacket.setAttribute(AttributeType.ARMOR, currentArmor);
                }
            }.runTaskLaterAsynchronously(Aradite.getInstance(), GLOWING_DURATION * 20);
        });
    }

    @Override
    public String getDescription() {
        return lang.getString("agents.innova.skills.ultimate.description");
    }

    @Override
    public SkillType getType() {
        return SkillType.ULTIMATE;
    }
}
