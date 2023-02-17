package com.github.tezvn.aradite.impl.team.type;


import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.Attacker;
import com.github.tezvn.aradite.impl.team.AbstractTeam;

/**
 * Attackers' objective is pushing the bomb cart to Defenders' base.
 * 
 * @author phongphong28
 */
public class AttackerImpl extends AbstractTeam implements Attacker {

	@Override
	public TeamRole getRole() {
		return TeamRole.ATTACK;
	}

}
