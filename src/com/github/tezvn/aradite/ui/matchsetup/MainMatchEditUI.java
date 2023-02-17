package com.github.tezvn.aradite.ui.matchsetup;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.Match;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.menu.elements.MenuItem;

public class MainMatchEditUI extends Menu {

    private static Language lang = Aradite.getInstance().getLanguage();

    public MainMatchEditUI(Match match) {
        super(9, lang.getString("ui.match-edit.main-menu.title"));

        setItem(2, new MenuItem(new ItemBuilder(Material.NAME_TAG)
                .setDisplayName(lang.getString("ui.match-edit.main-menu.id-item")
                        .replace("%id%", match.getUniqueID()))){
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
            }
        });

        setItem(3, new MenuItem(new ItemBuilder(Material.FILLED_MAP)
                .setDisplayName(lang.getString("ui.match-edit.main-menu.location.display-name"))
                .setLore(lang.getList("ui.match-edit.main-menu.location.lore"))){
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                Menu.open(player, new MatchLocationSetupUI(match));
            }
        });

    }

}
