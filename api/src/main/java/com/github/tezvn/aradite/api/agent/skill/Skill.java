package com.github.tezvn.aradite.api.agent.skill;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.match.Match;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.math.PrimaryMath;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The ability that a agent posesses.
 *
 * @author phongphong28
 */
public interface Skill {

    NamespacedKey SKILL_DATA = new NamespacedKey("aradite", "skill-data");
    String X_TEXTURE = "e6f1898f1e84805694544944f8b49c7007622a2d9b2bb59a278519a68991ac69",
            C_TEXTURE = "68622281c7910bd66f457b91e3c451cfdcc0adc1f3f9f10e8b30c2f54222c78d",
            ULT_TEXTURE = "4557ad657feb7b8b54cce290849f8482378e149ec21d271f5ff184f31885e917";

    List<String> registeredListener = Lists.newArrayList();

    /**
     * Return the agent that possesses the skill.
     */
    Agents getOwner();

    /**
     * Set the display name for the skill.
     *
     * @param displayName New display name
     */
    void setDisplayName(String displayName);

    /**
     * Set the id for the skill.
     *
     * @param id New ID
     */
    void setID(String id);

    /**
     * Return the display name of the skill.
     */
    String getDisplayName();

    /**
     * Return the ID of the skill.
     */
    String getID();

    /**
     * Each skill has some special stats. Those stats will be improved when the
     * skill is upgraded. So we need some math expressions to calculate them.<br>
     * Each stat will have its own expression, and can be converted into
     * {@code double} value by using {@link PrimaryMath#eval(String)}.
     */
    Map<String, String> getSkillEvalExpressions();

    /**
     * The skill's displaying icon on player's hotbar.
     */
    //TODO TEMPORARY ICON FOR TESTING
    default ItemStack getIcon() {
        String skillColor = getType() == SkillType.ACTIVE_X ? "§c" : (getType() == SkillType.ACTIVE_C ? "§b" : "§6§l");
        ItemStack item = new ItemBuilder(Material.SLIME_BALL).setDisplayName(skillColor + getDisplayName())
                .addLoreLine("§7Nhấn để kích hoạt chiêu này.")
                .create();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(SKILL_DATA, PersistentDataType.STRING,
                getOwner().toString() + "•" + getType().toString());
        meta.setLore(Arrays.asList(ChatPaginator.wordWrap(getDescription(), 28)));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Set the calculating expression for the given {@code key}.
     *
     * @param key        The key
     * @param expression The expression
     */
    void setEvalExpression(String key, String expression);

    /**
     * Actions that will be performed when activating the skill.
     *
     * @param level        The current level of the skill. Maximum is 3.
     * @param match        The match that player is currently in.
     * @param agent        Player who activated the skill.
     * @param targetEntity Living Entity which is the target of the skill
     * @param targetBlock  Block which is the target of the skill
     */
    void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock);

    /**
     * Return the description of the skill.<br>
     * It can have some placeholders which will be replaced with calculated
     * expressions through {@link #getSkillEvalExpressions()}. You have set the
     * placeholder as %<expression_key>%.
     */
    String getDescription();

    /**
     * Return the type of the skill.
     */
    SkillType getType();

    /**
     * Register a {@link Listener} for custom events that may be necessary for
     * operating the skill. Each listener will only be registered once.
     *
     * @param listener The listener
     */
    default void registerListener(Plugin plugin, Listener listener) {
        if (registeredListener.contains(getID())) return;
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        System.out.println("Register event for skill " + getID());
        registeredListener.add(getID());
    }
}
