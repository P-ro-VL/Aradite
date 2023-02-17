package com.github.tezvn.aradite.api.match;

/**
 * Phases of the match.
 * 
 * @author phongphong28
 */
public enum MatchPhase {

	COUNTDOWN_TO_START,
	TEAM_DIVIDE,
	AGENT_SELECT,
	WEAPON_SELECT,
	INGAME_CAPTURE_PHASE,
	INGAME_BOMB_CART_PHASE,
	INGAME_EVENT_PHASE,
	CALCULATING,
	RESTARTING, WAITING;
	
}
