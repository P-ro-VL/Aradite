package com.github.tezvn.aradite.impl.listener.setting;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.world.MatchLocationType;
import com.github.tezvn.aradite.api.world.MatchMap;
import com.github.tezvn.aradite.impl.ui.matchsetup.MatchLocationSetupUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pdx.mantlecore.java.Pair;
import pdx.mantlecore.menu.Menu;

import java.util.UUID;

public class MatchLocationSettingModule implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEdit(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = e.getMessage();

        if (!MatchLocationSetupUI.currentlySetting.containsKey(uuid)) return;

        if (message.equalsIgnoreCase("here")) {
            e.setCancelled(true);

            Pair<MatchLocationSetupUI, MatchLocationType> pairData = MatchLocationSetupUI.currentlySetting.get(uuid);
            MatchLocationSetupUI ui = pairData.getKey();

            Match match = ui.getMatch();
            MatchMap matchMap = match.getMatchMap();
            MatchLocationType locationType = pairData.getValue();

            Location location = player.getLocation();
            matchMap.registerLocation(locationType, location);

            Menu.open(player, new MatchLocationSetupUI(match));

            MatchLocationSetupUI.currentlySetting.remove(uuid);
        }
    }

}
