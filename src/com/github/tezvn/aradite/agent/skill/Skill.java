package com.github.tezvn.aradite.agent.skill;

import java.util.Map;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;
import com.google.common.collect.Maps;

import com.github.tezvn.aradite.agent.Agents;
import org.bukkit.inventory.ItemStack;

public abstract class Skill implements ISkill {

	public static Language lang = Aradite.getInstance().getLanguage();

	private String id;
	private String displayName;
	private Map<String, String> skillEvalExpressions = Maps.newHashMap();
	private Agents owner;

	public Skill(String ID, String displayName, Agents owner) {
		this.id = ID;
		this.displayName = displayName;
		this.owner = owner;
	}
	
	@Override
	public Agents getOwner() {
		return this.owner;
	}
	
	@Override
	public String getID() {
		return this.id;
	}
	
	@Override
	public String getDisplayName() {
		return this.displayName;
	}
	
	@Override
	public Map<String, String> getSkillEvalExpressions() {
		return this.skillEvalExpressions;
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	@Override
	public void setEvalExpression(String key, String expression) {
		this.skillEvalExpressions.put(key, expression);
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
