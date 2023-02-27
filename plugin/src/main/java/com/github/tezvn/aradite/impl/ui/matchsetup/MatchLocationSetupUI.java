package com.github.tezvn.aradite.impl.ui.matchsetup;

import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.world.MatchLocationType;
import com.github.tezvn.aradite.api.world.MatchMap;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.java.Pair;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.menu.elements.MenuItem;

import java.util.Map;
import java.util.UUID;

public class MatchLocationSetupUI extends Menu {

    public static Map<UUID, Pair<MatchLocationSetupUI, MatchLocationType>> currentlySetting = Maps.newHashMap();
    private static Language lang =  AraditeImpl.getInstance().getLanguage();
    private final Match match;

    public MatchLocationSetupUI(Match match) {
        super(9, lang.getString("ui.match-edit.location-menu.title"));

        this.match = match;
        MatchMap map = match.getMatchMap();

        int i = 0;
        for (MatchLocationType type : MatchLocationType.values()) {
            Material material = map.hasRegistered(type) ? (type.isMultiloc() ? Material.YELLOW_WOOL :
                    Material.LIME_WOOL) : Material.RED_WOOL;
            ItemBuilder itemBuilder = new ItemBuilder(material);
            itemBuilder.setDisplayName(lang.getString("ui.match-edit.location-menu.location-type-name."
                    + type.toString()));

            if (material != Material.RED_WOOL)
                map.getLocation(type).forEach(location -> itemBuilder.addLoreLine("§f- "
                        + formatLocation(location)));

            itemBuilder.addLoreLine("");
            itemBuilder.addLoreLine(lang.getString("ui.match-edit.location-menu.lore-reset"));

            boolean reachMaxSize = false;

            if (map.getLocation(type) != null && map.getLocation(type).size() == type.getMaxSize()) {
                itemBuilder.addLoreLine(lang.getString("ui.match-edit.location-menu.reach-max-size"));
                reachMaxSize = true;
            } else {
                itemBuilder.addLoreLine(lang.getString("ui.match-edit.location-menu.lore-multiloc-"
                        + type.isMultiloc()));
            }

            boolean finalReachMaxSize = reachMaxSize;
            MatchLocationSetupUI ui = this;
            setItem(i, new MenuItem(itemBuilder) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    Player player = (Player) e.getWhoClicked();

                    if (e.getClick() == ClickType.MIDDLE) {
                        map.unregister(type);
                        Menu.open(player, new MatchLocationSetupUI(match));
                    } else if (e.getClick() == ClickType.RIGHT) {
                        if (finalReachMaxSize) {
                            player.sendMessage(lang.getString("ui.match-edit.location-menu.reach-max-size"));
                            player.playSound(player.getEyeLocation(), Sound.ENTITY_BLAZE_DEATH, 2, 2);
                        } else {
                            Pair<MatchLocationSetupUI, MatchLocationType> pairData = Pair.of(ui, type);
                            currentlySetting.put(player.getUniqueId(), pairData);

                            player.closeInventory();
                            player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);

                            player.sendMessage(
                                    lang.getString("ui.match-edit.location-menu.set-location-instruction"));
                        }
                    }

                }
            });
            i++;
        }

    }

    public Match getMatch() {
        return match;
    }

    private String formatLocation(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY()
                + "," + location.getBlockZ();
    }

}
