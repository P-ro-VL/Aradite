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
	public String getID();

	/**
	 * Return the theme color of the agent.
	 */
	public Color getThemeColor();

	/**
	 * Return the display name of the agent.
	 */
	public String getDisplayName();

	/**
	 * Return all the skills of the agent.
	 */
	public Map<SkillType, Skill> getSkills();

	/**
	 * Return all the textures of the agent.
	 */
	public Map<TextureType, Texture> getTextures();

	/**
	 * Change the agent's ID.
	 */
	public void setID(String ID);

	/**
	 * Change the agent theme color
	 * @param color New theme color
	 */
	public void setThemeColor(Color color);

	/**
	 * Change the agent's display name.
	 */
	public void setDisplayName(String name);

	/**
	 * Change the texture of a specific agent's texture type.
	 * 
	 * @param type
	 *            Texture type
	 * @param texture
	 *            Texture value
	 */
	public void setTexture(TextureType type, Texture texture);

	/**
	 * Set the skill for the agent
	 * 
	 * @param type
	 *            Skill type
	 * @param skill
	 *            Skill
	 */
	public void setSkill(SkillType type, Skill skill);

	/**
	 * Bind the skill item to the player's inventory.
	 * @param player The player
	 */
	public void bindSkill(Player player);

}
