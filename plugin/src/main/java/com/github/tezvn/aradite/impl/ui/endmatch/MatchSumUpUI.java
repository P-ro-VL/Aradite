package com.github.tezvn.aradite.impl.ui.endmatch;

import com.github.tezvn.aradite.api.agent.Agent;
import com.github.tezvn.aradite.api.agent.texture.TextureType;
import com.github.tezvn.aradite.api.data.DataController;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.MedalRank;
import com.github.tezvn.aradite.api.packet.type.PlayerInGameData;
import com.github.tezvn.aradite.api.team.MatchTeam;
import com.github.tezvn.aradite.api.team.TeamRole;
import com.github.tezvn.aradite.api.weapon.Weapon;
import com.github.tezvn.aradite.api.weapon.WeaponType;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.data.DataControllerImpl;
import com.github.tezvn.aradite.api.data.Statistic;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameDataImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameMVPPacketImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerPreGameSelectPacketImpl;
import com.github.tezvn.aradite.api.language.Placeholder;
import com.github.tezvn.aradite.impl.team.MatchTeamImpl;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.menu.elements.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchSumUpUI extends Menu {

    public static final String MATCH_SUM_UP_UI_ID = ":offset_-16::match_sum_up_ui:";

    private final Language lang = AraditeImpl.getInstance().getLanguage();
    private ItemStack WINNING_TEAM_MVP_MEDAL = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName(lang.getString("ui.match-sum-up.mvp.winning"))
            .setTextureURL("33178d01aaf11e5ccfa38b190d7085ff37dd048d09b664f02570776f9b981466")
            .create();
    private ItemStack LOSING_TEAM_MVP_MEDAL = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName(lang.getString("ui.match-sum-up.mvp.losing"))
            .setTextureURL("337d7e473188703c1c8750a8353624b6a2d13055b65bf86b9ee87d88fdd677ef")
            .create();
    private ItemStack POG_MEDAL = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName(lang.getString("ui.match-sum-up.mvp.pog"))
            .setTextureURL("15a7ee9a86de203674a93b570bc8992e500363dd32f2b9813daaeeabccf92151")
            .create();

    private ItemStack MAIN_WEAPON_ARROW = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName(lang.getString("weapon.main"))
            .setTextureURL("117f3666d3cedfae57778c78230d480c719fd5f65ffa2ad3255385e433b86e")
            .create();

    private ItemStack SUB_WEAPON_ARROW = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName(lang.getString("weapon.sub"))
            .setTextureURL("2671c4c04337c38a5c7f31a5c751f991e96c03df730cdbee99320655c19d")
            .create();

    private int PLAYER_AVATAR_SLOT = 0, MAIN_WEAPON_ARROW_SLOT = 20, SUB_WEAPON_ARROW_SLOT = 29;
    private int[] PLAYERS_SLOTS = {12, 13, 14, 15, 16, 17};

    public MatchSumUpUI(Match match, Player player) {
        super(54, MATCH_SUM_UP_UI_ID);

        setItem(MAIN_WEAPON_ARROW_SLOT, new MenuItem(MAIN_WEAPON_ARROW){
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
            }
        });

        setItem(SUB_WEAPON_ARROW_SLOT, new MenuItem(SUB_WEAPON_ARROW){
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
            }
        });

        int atkWinRounds = match.getMatchScore().getScore(true, TeamRole.ATTACK);
        int defWinRounds = match.getMatchScore().getScore(true, TeamRole.DEFEND);

        DataController dataController = AraditeImpl.getInstance().getDataController();
        TeamRole winningTeam = atkWinRounds > defWinRounds ? TeamRole.ATTACK : TeamRole.DEFEND;

        MatchTeam matchTeam = match.getMatchTeam();
        Map.Entry<Player, Double> atkMVP = matchTeam.getMVPOf(TeamRole.ATTACK);
        Map.Entry<Player, Double> defMVP = matchTeam.getMVPOf(TeamRole.DEFEND);
        Map.Entry<Player, Double> pog = matchTeam.getPOG();

        int slotIndex = 0;
        for (Player agent : match.getMatchTeam().getAllPlayers()) {
            int agentSlot = PLAYERS_SLOTS[slotIndex];

            PlayerInGameData inGameData = match.retrieveProtocol(agent);
            PlayerPreGameSelectPacketImpl preGameSelectPacket = inGameData.getPacket(PlayerPreGameSelectPacketImpl.class);
            PlayerInGameMVPPacketImpl mvpPacket = inGameData.getPacket(PlayerInGameMVPPacketImpl.class);

            Agent pickedAgent = preGameSelectPacket.getSelectedAgent();
            Weapon mainWeapon = preGameSelectPacket.getSelectedWeapon(WeaponType.MAIN);
            Weapon subWeapon = preGameSelectPacket.getSelectedWeapon(WeaponType.SUB);
            String mainWeaponSkin = preGameSelectPacket.getSelectedWeaponSkin(WeaponType.MAIN);
            String subWeaponSkin = preGameSelectPacket.getSelectedWeaponSkin(WeaponType.SUB);

            String agentTexture = pickedAgent.getTextures().get(TextureType.SKULL).getData();
            double mvpPoint = 0;
            String teamColor = match.getMatchTeam().getTeamColor(player);

            String kda = mvpPacket.getStatistic(Statistic.KILL) + "/" + mvpPacket.getStatistic(Statistic.DEATH) + "/"
                    + mvpPacket.getStatistic(Statistic.DEATH);

            List<String> medals = new ArrayList<>();
            for (PlayerInGameMVPPacketImpl.MVPStatistics mvpStatistics : PlayerInGameMVPPacketImpl.MVPStatistics.values()) {
                MedalRank rank = mvpStatistics.getRankWithGivenPoint(mvpPacket.getMVPPoint(mvpStatistics));
                if(rank != null){
                    medals.add(lang.getString("ui.match-sum-up.medal")
                            .replaceAll("%rank_color%", rank.getColor())
                            .replaceAll("%statistic_icon%", mvpStatistics.getMedalIcon())
                            .replaceAll("%statistic%", mvpStatistics.getDescription())
                            .replaceAll("%rank%", rank.getDisplayName()));
                }
            }

            List<String> agentAvatarLore = lang.getListWithPlaceholders("ui.match-sum-up.player-detail",
                    Placeholder.of("kda", kda),
                    Placeholder.of("team_color", teamColor),
                    Placeholder.of("mvp_point", "" + mvpPoint),
                    Placeholder.of("medal", medals),
                    Placeholder.of("team", matchTeam.getTeamOf(player).getRole().getDisplay()));

            ItemStack agentAvatar = new ItemBuilder(Material.PLAYER_HEAD)
                    .setDisplayName(teamColor + agent.getName())
                    .setLore(agentAvatarLore)
                    .setTextureURL(agentTexture)
                    .create();
            setItem(agentSlot, new MenuItem(agentAvatar) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });

            TeamRole agentTeam = matchTeam.getPlayerTeam(agent);

            String agentName = agent.getName();
            boolean isMVP = atkMVP.getKey().getName().equals(agentName) || defMVP.getKey().getName().equals(agentName);
            ItemStack medal = new ItemStack(Material.AIR);

            if (isMVP) {
                medal = winningTeam == agentTeam ? WINNING_TEAM_MVP_MEDAL : LOSING_TEAM_MVP_MEDAL;
            }

            if (pog.getKey().getName().equals(agentName)) medal = POG_MEDAL;
            setItem(agentSlot - 9, new MenuItem(medal) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });

            setItem(agentSlot + 9, new MenuItem(mainWeapon.toItemStack(mainWeaponSkin)) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });

            setItem(agentSlot + 9 * 2, new MenuItem(subWeapon.toItemStack(subWeaponSkin)) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });
            slotIndex++;
        }

    }

}
