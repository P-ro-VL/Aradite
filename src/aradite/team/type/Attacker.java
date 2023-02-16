package aradite.team.type;

import aradite.team.AbstractTeam;
import aradite.team.TeamRole;

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
