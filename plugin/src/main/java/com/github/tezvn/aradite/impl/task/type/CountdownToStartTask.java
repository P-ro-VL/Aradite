package com.github.tezvn.aradite.impl.task.type;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.task.AsyncTimerTask;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CountdownToStartTask extends AsyncTimerTask {

	public static final short SECONDS_TO_START = 10;

	private Match match;
	private Language lang = AraditeImpl.getInstance().getLanguage();

	private int count;

	public CountdownToStartTask(Match match) {
		super(TimeUnit.SECONDS, 1, "countdown-to-start-" + match.getUniqueID());
		this.match = match;
		this.count = SECONDS_TO_START;
	}

	@Override
	public void onExecute() {
		List<Player> waitingPlayers = match.getWaitingPlayers();
		waitingPlayers.forEach(player -> {
			player.sendMessage("");
			player.sendMessage(lang.getString("match.preparation.start-countdown-broadcast"));
			player.sendMessage("");
		});
	}

	@Override
	public void run() {
		if (count < 0) {
			match.start();
			this.cancel();
			return;
		}

		List<Player> waitingPlayers = match.getWaitingPlayers();
		waitingPlayers.forEach(player -> {
			String title = lang.getString("match.preparation.start-countdown-title");
			String subtitle = lang.getString("match.preparation.start-countdown-subtitle").replaceAll("%0", "" + count);

			player.sendTitle(title, subtitle);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1);
		});
		count--;
	}

}
