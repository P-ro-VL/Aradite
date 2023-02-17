package com.github.tezvn.aradite.impl.data;

import com.github.tezvn.aradite.impl.data.global.PlayerDataStorage;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

/**
 * The control center for all in and out data stream of VALORANT.<br>
 * VALORANT's data is mainly processed and stored in both local database and SQL
 * database.
 * 
 * @author phongphong28
 */
public class DataController {

	private final Map<UUID, PlayerDataStorage> userData = Maps.newHashMap();

	/**
	 * Return the data of player whose {@link UUID} is {@code uuid}.
	 * 
	 * @param uuid
	 *            Player's uuid
	 * @return Player's data
	 */
	public PlayerDataStorage getUserData(UUID uuid) {
		PlayerDataStorage data = userData.get(uuid);
		if(data == null) {
			data = new PlayerDataStorage(uuid);
			userData.put(uuid, data);
		}
		return data;
	}

}
