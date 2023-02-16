package aradite.team.type;

import aradite.team.AbstractTeam;
import aradite.team.TeamRole;

public class Observer extends AbstractTeam {

	@Override
	public TeamRole getRole() {
		return TeamRole.OBSERVER;
	}

}
