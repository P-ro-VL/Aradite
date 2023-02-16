package aradite.team;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import pdx.mantlecore.java.collection.Lists;

/**
 * Indicates all actions and attributes to control arena's teams.
 * 
 * @author phongphong28
 */
public abstract class AbstractTeam implements Team {

	private List<Player> members = Collections.synchronizedList(Lists.newArrayList());

	@Override
	public List<Player> getMembers() {
		return members;
	}

	@Override
	public int size() {
		return (int) getMembers().stream().filter(member -> member.isOnline()).count();
	}

	@Override
	public void addMember(Player player) {
		members.add(player);
	}

	@Override
	public void addAll(List<Player> uuids) {
		uuids.forEach(uuid -> addMember(uuid));
	}

	@Override
	public void clear() {
		members.clear();
	}

}
