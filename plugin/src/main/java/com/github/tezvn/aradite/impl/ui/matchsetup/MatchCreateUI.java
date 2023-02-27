package com.github.tezvn.aradite.impl.ui.matchsetup;

import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.MatchType;
import com.github.tezvn.aradite.api.world.MapType;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.match.MatchImpl;
import com.github.tezvn.aradite.impl.match.MatchManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.menu.elements.MenuButton;
import pdx.mantlecore.menu.elements.MenuItem;

public class MatchCreateUI extends Menu {

    private static Language lang = AraditeImpl.getInstance().getLanguage();

    private MatchType finalMatchType = MatchType.NORMAL;
    private MapType finalMapType = MapType.ASCENT;

    public MatchCreateUI() {
        super(9, lang.getString("ui.match-edit.create-match.title"));

        MatchManager matchManager = AraditeImpl.getInstance().getMatchManager();
        String path = "ui.match-edit.create-match.";

        MenuButton<MatchType> typeMenuButton = new MenuButton<>();
        for (MatchType matchType : MatchType.values())
            typeMenuButton.appendData(new ItemBuilder(matchType.getIcon(),
                            lang.getString(path + "match-type").replace("%display%",
                                    lang.getString("match.match-type." + matchType.toString())),
                            "")
                            .setLore(lang.getList(path + "click-to-edit")).create(),
                    new MenuButton.DataPerform<MatchType>(MatchType.NORMAL) {
                        @Override
                        public void onSelected(InventoryClickEvent e) {
                            e.setCancelled(true);
                            finalMatchType = matchType;
                        }
                    });

        MenuButton<MapType> mapTypeMenuButton = new MenuButton<>();
        for (MapType mapType : MapType.values())
            mapTypeMenuButton.appendData(new ItemBuilder(Material.MAP)
                    .setDisplayName(lang.getString(path + "map-type").replace("%display%", mapType.toString()))
                    .setLore(lang.getList("ui.match-edit.create-match.click-to-edit"))
                    .create(), new MenuButton.DataPerform<MapType>(mapType) {
                @Override
                public void onSelected(InventoryClickEvent e) {
                    e.setCancelled(true);
                    finalMapType = mapType;
                }
            });

        setItem(0, typeMenuButton);
        setItem(1, mapTypeMenuButton);
        setItem(8, new MenuItem(
                new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(lang.getString(path + "done.displayname"))
                        .setLore(lang.getList(path + "done.lore"))
                        .setTextureURL("a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6")
        ) {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();

                if(finalMapType == null || finalMatchType == null){
                    player.playSound(player.getEyeLocation(), Sound.ENTITY_BLAZE_DEATH, 2, 2);
                    return;
                }

                Match match = new MatchImpl(finalMatchType, finalMapType);
                matchManager.registerMatch(match);

                player.closeInventory();
                player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                player.sendTitle(lang.getString(path + "success.title"),
                        lang.getString(path + "success.sub-title").replace("%id%", match.getUniqueID()));
            }
        });
    }
}
