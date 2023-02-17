package com.github.tezvn.aradite.team.type;

import com.github.tezvn.aradite.team.AbstractTeam;
import com.github.tezvn.aradite.team.TeamRole;

/**
 * Attackers' objective is pushing the bomb cart to Defenders' base.
 * 
 * @author phongphong28
 */
public class Attacker extends AbstractTeam {

	@Override
	public TeamRole getRole() {
		return TeamRole.ATTACK;
	}

}
