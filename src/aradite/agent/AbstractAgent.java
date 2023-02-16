package aradite.agent;

import java.util.Map;

import com.google.common.collect.Maps;

import aradite.agent.attribute.Attribute;
import aradite.agent.skill.Skill;
import aradite.agent.skill.SkillType;
import aradite.agent.texture.Texture;
import aradite.agent.texture.TextureType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pdx.mantlecore.item.ItemBuilder;

public abstract class AbstractAgent implements Agent {

	private String id;
	private String displayName;
	private Map<SkillType, Skill> skills = Maps.newHashMap();
	private Map<TextureType, Texture> textures = Maps.newHashMap();
	private Color themeColor;

	public AbstractAgent(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	@Override
	public Color getThemeColor() {
		return themeColor;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public Map<SkillType, Skill> getSkills() {
		return skills;
	}

	@Override
	public Map<TextureType, Texture> getTextures() {
		return textures;
	}

	@Override
	public void setID(String ID) {
		this.id = ID;
	}

	@Override
	public void setDisplayName(String name) {
		this.displayName = name;
	}

	@Override
	public void setTexture(TextureType type, Texture texture) {
		this.textures.put(type, texture);
	}

	@Override
	public void setSkill(SkillType type, Skill skill) {
		this.skills.put(type, skill);
	}

	@Override
	public void setThemeColor(Color themeColor) {
		this.themeColor = themeColor;
	}

	@Override
	public void bindSkill(Player player) {
		Skill Xskill = getSkills().get(SkillType.ACTIVE_X);
		Skill Cskill = getSkills().get(SkillType.ACTIVE_C);
		Skill ULTskill = getSkills().get(SkillType.ULTIMATE);

		PlayerInventory inventory = player.getInventory();
		inventory.setItem(3, Xskill.getIcon());
		inventory.setItem(4, ULTskill.getIcon());
		inventory.setItem(5, Cskill.getIcon());
		
		ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§e§l" + getDisplayName())
				.setTextureURL(getTextures().get(TextureType.SKULL).getData()).create();
		inventory.setHelmet(head);
		inventory.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor(getThemeColor()).create());
		inventory.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherColor(getThemeColor()).create());
		inventory.setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor(getThemeColor()).create());
	}
}
