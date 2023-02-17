package com.github.tezvn.aradite.team.type;

import com.github.tezvn.aradite.team.AbstractTeam;
import com.github.tezvn.aradite.team.TeamRole;

public class Observer extends AbstractTeam {

	@Override
	public TeamRole getRole() {
		return TeamRole.OBSERVER;
	}

}
