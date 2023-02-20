package com.github.tezvn.aradite.impl.listener.ingame;

import com.github.tezvn.aradite.api.agent.Agent;
import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.match.MatchManager;
import com.github.tezvn.aradite.impl.util.LocationUtils;
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
import org.bukkit.util.RayTraceResult;

public class AgentSkillModule implements Listener {

    @EventHandler
    public void onPlayerCastSkill(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        MatchManager matchManager =  AraditeImpl.getInstance().getMatchManager();
        Match match = matchManager.getMatch(player);
        if (match == null) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(Skill.SKILL_DATA, PersistentDataType.STRING)) return;

        String data = dataContainer.get(Skill.SKILL_DATA, PersistentDataType.STRING);
        String[] splittedData = data.split("•");

        Agents agentType = Agents.valueOf(splittedData[0]);
        SkillType skillType = SkillType.valueOf(splittedData[1]);

        try {
            e.setCancelled(true);
            Agent agentInstance = AraditeImpl.getInstance().getAgentManager().createNewInstance(agentType);
            Skill skill = agentInstance.getSkills().get(skillType);

            RayTraceResult rayTraceResult = LocationUtils.rayTraceEntities(player.getEyeLocation(),
                    player.getEyeLocation().getDirection(), 6, 1, (entity) -> true);
            if(rayTraceResult == null)
                return;
            Entity lookAtEntity = rayTraceResult.getHitEntity();
            Block lookAtBlock = player.getTargetBlockExact(6);

            LivingEntity target = lookAtEntity instanceof LivingEntity ? (LivingEntity) lookAtEntity : null;
            Block block = lookAtBlock != null && lookAtBlock.getType().isSolid() ? lookAtBlock : null;

            skill.onActivate(1, match, player, target, block);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
