package com.github.tezvn.aradite.team;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Teams are groups containing 3 players who are identified as an agent. There
 * are two teams in a traditional match : Attacker and Defender.<br>
 * • Attacker : Plant the spike or kill all defenders to win.<br>
 * • Defender : Defuse the spike or kill all attackers to win.<br>
 * 
 * @author phongphong28
 */
public interface Team {

	/**
	 * Return a list of team members as {@link Player}.
	 */
	public List<Player> getMembers();

	/**
	 * Add a new player to the team.
	 * 
	 * @param uniqueId
	 *            Player's Player
	 */
	public void addMember(Player uniqueId);

	/**
	 * Instantly add a {@link List} of players to team.
	 * 
	 * @param uuids
	 *            Players' Player
	 */
	public void addAll(List<Player> uuids);

	/**
	 * Return the number of online members in the team.
	 */
	public int size();

	/**
	 * In the first 3 rounds, there is no difference between teams. They compete
	 * together at capture phases to get roles in the next 3 rounds. The winning
	 * team in those rounds will become {@link TeamRole#ATTACK ATTACK}ers, and the
	 * other will be {@link TeamRole#DEFEND DEFEND}ers.
	 */
	public TeamRole getRole();

	/**
	 * Remove all players from the team.
	 */
	public void clear();

}
