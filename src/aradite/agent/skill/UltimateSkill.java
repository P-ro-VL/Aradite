package aradite.agent.skill;

import aradite.agent.Agents;

/**
 * Skill that has the most powerful damage and a big influence on the combat.
 * 
 * @author phongphong28
 */
public abstract class UltimateSkill extends Skill {

	public UltimateSkill(String ID, String displayName, Agents owner) {
		super(ID, displayName, owner);
	}

}
