package com.github.tezvn.aradite.team.type;

import com.github.tezvn.aradite.team.AbstractTeam;
import com.github.tezvn.aradite.team.TeamRole;

/**
 * Defenders' objective is defusing the bomb on the cart or killing all attackers so as to
 * win the game.
 * 
 * @author phongphong28
 */
public class Defender extends AbstractTeam {

	@Override
	public TeamRole getRole() {
		return TeamRole.DEFEND;
	}

}
