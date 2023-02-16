package aradite.agent.skill;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;

import aradite.Aradite;
import aradite.agent.Agents;
import aradite.match.Match;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.math.PrimaryMath;

/**
 * The ability that a agent posesses.
 *
 * @author phongphong28
 */
public interface ISkill {

    public static NamespacedKey SKILL_DATA = new NamespacedKey(Aradite.getInstance(), "skill-data");
    public static String X_TEXTURE = "e6f1898f1e84805694544944f8b49c7007622a2d9b2bb59a278519a68991ac69",
            C_TEXTURE = "68622281c7910bd66f457b91e3c451cfdcc0adc1f3f9f10e8b30c2f54222c78d",
            ULT_TEXTURE = "4557ad657feb7b8b54cce290849f8482378e149ec21d271f5ff184f31885e917";

    static final List<String> registeredListener = Lists.newArrayList();

    /**
     * Return the agent that possesses the skill.
     */
    public Agents getOwner();

    /**
     * Set the display name for the skill.
     *
     * @param displayName New display name
     */
    public void setDisplayName(String displayName);

    /**
     * Set the id for the skill.
     *
     * @param id New ID
     */
    public void setID(String id);

    /**
     * Return the display name of the skill.
     */
    public String getDisplayName();

    /**
     * Return the ID of the skill.
     */
    public String getID();

    /**
     * Each skill has some special stats. Those stats will be improved when the
     * skill is upgraded. So we need some math expressions to calculate them.<br>
     * Each stat will have its own expression, and can be converted into
     * {@code double} value by using {@link PrimaryMath#eval(String)}.
     */
    public Map<String, String> getSkillEvalExpressions();

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
    public void setEvalExpression(String key, String expression);

    /**
     * Actions that will be performed when activating the skill.
     *
     * @param level        The current level of the skill. Maximum is 3.
     * @param match        The match that player is currently in.
     * @param agent        Player who activated the skill.
     * @param targetEntity Living Entity which is the target of the skill
     * @param targetBlock  Block which is the target of the skill
     */
    public void onActivate(int level, Match match, Player agent, LivingEntity targetEntity, Block targetBlock);

    /**
     * Return the description of the skill.<br>
     * It can have some placeholders which will be replaced with calculated
     * expressions through {@link #getSkillEvalExpressions()}. You have set the
     * placeholder as %<expression_key>%.
     */
    public String getDescription();

    /**
     * Return the type of the skill.
     */
    public SkillType getType();

    /**
     * Register a {@link Listener} for custom events that may be necessary for
     * operating the skill. Each listener will only be registered once.
     *
     * @param listener The listener
     */
    default void registerListener(Listener listener) {
        if (registeredListener.contains(getID())) return;
        Bukkit.getPluginManager().registerEvents(listener, Aradite.getInstance());
        System.out.println("Register event for skill " + getID());
        registeredListener.add(getID());
    }
}
