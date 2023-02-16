package aradite.listener.setting;

import aradite.match.Match;
import aradite.ui.matchsetup.MatchLocationSetupUI;
import aradite.world.MatchLocationType;
import aradite.world.MatchMap;
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
