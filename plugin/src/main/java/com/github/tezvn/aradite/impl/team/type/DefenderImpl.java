package com.github.tezvn.aradite.impl.team.type;


import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.Defender;
import com.github.tezvn.aradite.impl.team.AbstractTeam;

/**
 * Defenders' objective is defusing the bomb on the cart or killing all attackers so as to
 * win the game.
 * 
 * @author phongphong28
 */
public class DefenderImpl extends AbstractTeam implements Defender {

	@Override
	public TeamRole getRole() {
		return TeamRole.DEFEND;
	}

}
