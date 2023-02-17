package com.github.tezvn.aradite.impl.agent.skill;

import com.github.tezvn.aradite.api.agent.Agent;
import com.github.tezvn.aradite.api.agent.skill.Skill;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.api.agent.Agents;
import com.github.tezvn.aradite.api.language.Language;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SkillImpl implements Skill {

	public static Language lang = AraditeImpl.getInstance().getLanguage();

	private String id;
	private String displayName;
	private final Map<String, String> skillEvalExpressions = Maps.newHashMap();
	private final Agents owner;

	public SkillImpl(String ID, String displayName, Agents owner) {
		this.id = ID;
		this.displayName = displayName;
		this.owner = owner;
	}
	
	@Override
	public Agents getOwner() {
		return this.owner;
	}
	
	@Override
	public String getID() {
		return this.id;
	}
	
	@Override
	public String getDisplayName() {
		return this.displayName;
	}
	
	@Override
	public Map<String, String> getSkillEvalExpressions() {
		return this.skillEvalExpressions;
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	@Override
	public void setEvalExpression(String key, String expression) {
		this.skillEvalExpressions.put(key, expression);
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Player getNearestEnemy(Location location, Match match, Player agent, int range) {
		return getNearbyPlayers(location, match, agent, range).get(0);
	}

	public List<Player> getNearbyPlayers(Location location, Match match, Player agent, int range) {
		return Objects.requireNonNull(location.getWorld()).getNearbyEntities(location,
				range, range, range, entity -> {
					if(!(entity instanceof Player))
						return false;
					Player p = (Player) entity;
					return !match.getMatchTeam().isOnSameTeam(p, agent);
				}).stream().map(entity -> (Player) entity).collect(Collectors.toList());
	}
}
