package com.github.tezvn.aradite.api.agent;

import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.agent.texture.Texture;
import com.github.tezvn.aradite.api.agent.texture.TextureType;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Agents are representatives for players in a match. Each agent has his/her own
 * skills.
 * 
 * @author phongphong28
 */
public interface Agent {

	/**
	 * Return ID of the agent.
	 */
	String getID();

	/**
	 * Return the theme color of the agent.
	 */
	Color getThemeColor();

	/**
	 * Return the display name of the agent.
	 */
	String getDisplayName();

	/**
	 * Return all the skills of the agent.
	 */
	Map<SkillType, Skill> getSkills();

	/**
	 * Return all the textures of the agent.
	 */
	Map<TextureType, Texture> getTextures();

	/**
	 * Change the agent's ID.
	 */
	void setID(String ID);

	/**
	 * Change the agent theme color
	 * @param color New theme color
	 */
	void setThemeColor(Color color);

	/**
	 * Change the agent's display name.
	 */
	void setDisplayName(String name);

	/**
	 * Change the texture of a specific agent's texture type.
	 * 
	 * @param type
	 *            Texture type
	 * @param texture
	 *            Texture value
	 */
	void setTexture(TextureType type, Texture texture);

	/**
	 * Set the skill for the agent
	 * 
	 * @param type
	 *            Skill type
	 * @param skill
	 *            Skill
	 */
	void setSkill(SkillType type, Skill skill);

	/**
	 * Bind the skill item to the player's inventory.
	 * @param player The player
	 */
	void bindSkill(Player player);

}
