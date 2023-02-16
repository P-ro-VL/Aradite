package aradite.team.type;

import aradite.team.AbstractTeam;
import aradite.team.TeamRole;

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
