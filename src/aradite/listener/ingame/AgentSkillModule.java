package aradite.listener.ingame;

import aradite.Aradite;
import aradite.agent.Agent;
import aradite.agent.Agents;
import aradite.agent.skill.ISkill;
import aradite.agent.skill.Skill;
import aradite.agent.skill.SkillType;
import aradite.match.Match;
import aradite.match.MatchManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AgentSkillModule implements Listener {

    @EventHandler
    public void onPlayerCastSkill(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        MatchManager matchManager = Aradite.getInstance().getMatchManager();
        Match match = matchManager.getMatch(player);
        if (match == null) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer == null) return;
        if (!dataContainer.has(ISkill.SKILL_DATA, PersistentDataType.STRING)) return;

        String data = dataContainer.get(ISkill.SKILL_DATA, PersistentDataType.STRING);
        String[] splittedData = data.split("•");

        Agents agentType = Agents.valueOf(splittedData[0]);
        SkillType skillType = SkillType.valueOf(splittedData[1]);

        try {
            e.setCancelled(true);
            Agent agentInstance = agentType.getWrapper().newInstance();
            Skill skill = agentInstance.getSkills().get(skillType);

            Entity lookAtEntity = player.getTargetEntity(6);
            Block lookAtBlock = player.getTargetBlock(6);

            LivingEntity target = lookAtEntity instanceof LivingEntity ? (LivingEntity) lookAtEntity : null;
            Block block = lookAtBlock != null && lookAtBlock.getType().isSolid() ? lookAtBlock : null;

            skill.onActivate(1, match, player, target, block);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
