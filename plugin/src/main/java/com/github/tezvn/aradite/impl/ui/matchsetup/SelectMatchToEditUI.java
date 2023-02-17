package com.github.tezvn.aradite.impl.ui.matchsetup;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.match.MatchManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.java.IntRange;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.menu.elements.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class SelectMatchToEditUI extends Menu {

    private static Language lang = AraditeImpl.getInstance().getLanguage();
    private static ItemStack createButton = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName(lang.getString("ui.match-edit.select-match.create-button.displayname"))
            .setLore(lang.getList("ui.match-edit.select-match.create-button.lore"))
            .setTextureURL("9aca891a7015cbba06e61c600861069fa7870dcf9b35b4fe15850f4b25b3ce0")
            .create();

    public SelectMatchToEditUI(int page) {
        super(54, lang.getString("ui.match-edit.select-match.title"));

        MatchManager matchManager = AraditeImpl.getInstance().getMatchManager();
        List<Match> matches = new ArrayList<>(matchManager.getAllAvailableMatches());

        for (int i = page * 45; i < (page + 1) * 45; i++) {
            int slot = (page > 0) ? i - (page * 45) : i;
            boolean equalSize = i == matches.size();
            if (i >= matches.size()) {
                if (equalSize)
                    setItem(slot, new MenuItem(createButton) {
                        @Override
                        public void onClick(InventoryClickEvent e) {
                            e.setCancelled(true);
                            Player player = (Player) e.getWhoClicked();
                            Menu.open(player, new MatchCreateUI());
                        }
                    });
                else
                    setItem(slot, new MenuItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", "") {
                        @Override
                        public void onClick(InventoryClickEvent e) {
                            e.setCancelled(true);
                        }
                    });
                continue;
            }

            Match match = matches.get(i);
            setItem(slot, new MenuItem(new ItemBuilder(Material.CHEST).setDisplayName("§a§l" + match.getUniqueID())
                    .addLoreLine(lang.getString("ui.match-edit.select-match.click-to-edit"))) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    Player player = (Player) e.getWhoClicked();
                    Menu.open(player, new MainMatchEditUI(match));
                    player.playSound(player.getEyeLocation(), Sound.ITEM_BOOK_PAGE_TURN, 2, 2);
                }
            });
        }

        for (int i : IntRange.of(45, 53)) {
            setItem(i, new MenuItem(Material.BLACK_STAINED_GLASS_PANE, " ", "") {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });
        }

        if (matches.size() / ((page + 1) * 6) > 1) {
            setItem(50, new MenuItem(new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.next-page"),
                    "").setTextureURL("65a84e6394baf8bd795fe747efc582cde9414fccf2f1c8608f1be18c0e079138")) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    Menu.open((Player) e.getWhoClicked(), new SelectMatchToEditUI(page + 1));
                }
            });
        }

        if (page > 0) {
            setItem(48, new MenuItem(new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.previous-page")
                    , "").setTextureURL("da53d04797b47a68484d111025d940a34886a0fa8dc806e7457024a87f1abd56")) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    Menu.open((Player) e.getWhoClicked(), new SelectMatchToEditUI(page - 1));
                }
            });
        }

    }
}
