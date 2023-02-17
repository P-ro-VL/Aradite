package com.github.tezvn.aradite.impl.team.type;


import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.Observer;
import com.github.tezvn.aradite.impl.team.AbstractTeam;

public class ObserverImpl extends AbstractTeam implements Observer {

	@Override
	public TeamRole getRole() {
		return TeamRole.OBSERVER;
	}

}
