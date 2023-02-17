package com.github.tezvn.aradite.impl.agent.skill;

import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.agent.skill.UltimateSkill;

/**
 * Skill that has the most powerful damage and a big influence on the combat.
 * 
 * @author phongphong28
 */
public abstract class UltimateSkillImpl extends SkillImpl implements UltimateSkill {

	public UltimateSkillImpl(String ID, String displayName, Agents owner) {
		super(ID, displayName, owner);
	}

}
