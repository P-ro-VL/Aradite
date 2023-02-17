package com.github.tezvn.aradite.team.type;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.github.tezvn.aradite.team.AbstractTeam;
import com.github.tezvn.aradite.team.TeamRole;

public class UndefinedTeam extends AbstractTeam {

	private Multimap<UndefinedTeamType, String> players = HashMultimap.create();

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
	public void addPlayer(Player player, UndefinedTeamType type) {
		this.players.put(type, player.getName());
	}

	@Override
	@Deprecated
	public void addMember(Player player) {
		throw new UnsupportedOperationException(
				"Cannot call addMember(Player) method in UndefinedTeam.class. Please use addPlayer(Player, UndefinedTeamType) instead !");
	}

	/**
	 * Return all players in the given team's {@code type}
	 * 
	 * @param type
	 *            Type of the team.
	 */
	public List<Player> getPlayerInTeam(UndefinedTeamType type) {
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
	public UndefinedTeamType getTeamOf(Player player) {
		String name = player.getName();
		for (java.util.Map.Entry<UndefinedTeamType, String> entry : this.players.entries()) {
			if (entry.getValue().equals(name))
				return entry.getKey();
		}
		return null;
	}

	public static enum UndefinedTeamType {
		A, B;

		public static UndefinedTeamType not(UndefinedTeamType type) {
			return type == A ? B : A;
		}
	}

}
