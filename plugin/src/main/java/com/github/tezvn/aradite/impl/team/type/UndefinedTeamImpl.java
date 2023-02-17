package com.github.tezvn.aradite.impl.team.type;

import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.team.type.UndefinedTeam;
import com.github.tezvn.aradite.impl.team.AbstractTeam;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class UndefinedTeamImpl extends AbstractTeam implements UndefinedTeam {

	private Multimap<UndefinedTeam.Type, String> players = HashMultimap.create();

	@Override
	public TeamRole getRole() {
		return TeamRole.UNDEFINED;
	}

	/**
	 * Add a player into a team.
	 * 
	 * @param player
	 *            Player
	 * @param type
	 *            Team
	 */
	public void addPlayer(Player player, UndefinedTeam.Type type) {
		this.players.put(type, player.getName());
	}

	@Override
	@Deprecated
	public void addMember(Player player) {
		throw new UnsupportedOperationException(
				"Cannot call addMember(Player) method in UndefinedTeam.class. Please use addPlayer(Player, UndefinedTeam.Type) instead !");
	}

	/**
	 * Return all players in the given team's {@code type}
	 * 
	 * @param type
	 *            Type of the team.
	 */
	public List<Player> getPlayerInTeam(UndefinedTeam.Type type) {
		return players.get(type).stream().map(string -> Bukkit.getPlayerExact(string)).filter(player -> {
			return player != null && player.isOnline();
		}).collect(Collectors.toList());
	}

	@Override
	public List<Player> getMembers() {
		return getPlayers();
	}

	/**
	 * Return all players in the team.
	 */
	public List<Player> getPlayers() {
		return players.values().stream().map(Bukkit::getPlayer).filter(player -> {
			return player != null && player.isOnline();
		}).collect(Collectors.toList());
	}

	/**
	 * Return the team that the given player is in.
	 * 
	 * @param player
	 *            The player
	 * @return The team he is in.
	 */
	public UndefinedTeam.Type getTeamOf(Player player) {
		String name = player.getName();
		for (java.util.Map.Entry<UndefinedTeam.Type, String> entry : this.players.entries()) {
			if (entry.getValue().equals(name))
				return entry.getKey();
		}
		return null;
	}

}
