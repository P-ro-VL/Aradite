package com.github.tezvn.aradite.ui.agentselect;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.tezvn.aradite.agent.AgentMasteryRank;
import com.github.tezvn.aradite.data.DataController;
import com.github.tezvn.aradite.data.global.PlayerDataStorage;
import com.github.tezvn.aradite.data.packet.PacketPackage;
import com.github.tezvn.aradite.data.packet.type.PlayerPreGameSelectPacket;
import com.github.tezvn.aradite.language.Placeholder;
import com.github.tezvn.aradite.weapon.Weapon;
import com.github.tezvn.aradite.weapon.WeaponManager;
import com.github.tezvn.aradite.weapon.WeaponType;
import com.github.tezvn.aradite.agent.Agent;
import com.github.tezvn.aradite.agent.Agents;
import com.github.tezvn.aradite.agent.texture.Texture;
import com.github.tezvn.aradite.agent.texture.TextureType;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.Match;
import com.github.tezvn.aradite.task.type.AgentSelectTask;
import com.github.tezvn.aradite.team.MatchTeam;
import com.github.tezvn.aradite.team.TeamRole;
import com.github.tezvn.aradite.team.type.UndefinedTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.github.tezvn.aradite.Aradite;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;
import pdx.mantlecore.item.ItemBuilder;
import pdx.mantlecore.java.Pair;
import pdx.mantlecore.java.StringUtils;
import pdx.mantlecore.math.RomanNumerals;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.menu.RunnableMenu;
import pdx.mantlecore.menu.elements.MenuItem;

public class AgentSelectUI extends RunnableMenu {

    public static final String AGENT_SELECT_UI_ID = ":offset_-16::agent_select_ui:";
    private static Language lang = Aradite.getInstance().getLanguage();
    private static ItemStack RIGHT_ARROW = new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.agent-select.is-picking"), "").setTextureURL("276b396a346fbd5dc98df9c06097bb591fefd768ed5b3bb90bb32af85eb91406").create();
    private static ItemStack LEFT_ARROW = new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.agent-select.is-picking"), "").setTextureURL("921217d7fee9faf2fbc741e0adb5de6a84ea7d5536cbccd1dbc8a323f95b990d").create();

    private static ItemStack MAIN_WEAPON_NEXT = new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.agent-select.weapon-select.next-weapon"), "").setTextureURL("74bfc66c5121351a526662fc5c15c35639abbbe3f9b17d55ef1fb5e68e6c8").create();
    private static ItemStack MAIN_WEAPON_PREVIOUS = new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.agent-select.weapon-select.previous-weapon"), "").setTextureURL("d7b99d7d3cba459e1a882447dacdb27943c1ef721b2aecacec669bad31cafa").create();

    private static ItemStack SUB_WEAPON_NEXT = new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.agent-select.weapon-select.next-weapon"), "").setTextureURL("63fabc3891c962c913959d65be90daa5de1211fa4087dae386ff4ed4248f").create();
    private static ItemStack SUB_WEAPON_PREVIOUS = new ItemBuilder(Material.PLAYER_HEAD, lang.getString("ui.agent-select.weapon-select.previous-weapon"), "").setTextureURL("91459cfc44cc51ddf27988596d2de8ac8556e93d7946219cf64c90c8c05fca").create();

    private int[] enemySlots = {17, 26, 35}, allySlots = {9, 18, 27}, arrowInstruction = {16, 25, 34, 10, 19, 28};
    private int countdownSlot = 4, currentPage = 0, pickedAgentAvatar = 13, mainWeapon = 40, subWeapon = 49;
    private int[] agentSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

    private List<Player> allies = Lists.newArrayList(), enemies = Lists.newArrayList();
    protected Player player;
    protected AgentSelectTask task;
    private PlayerPreGameSelectPacket packet;
    private PacketPackage<String> packetPackage;
    protected Match match;

    private Map<String, Pair<Integer, Agents>> selectData = Maps.newHashMap();
    private String oldName = "";

    private boolean updated = false;
    private int mainWeaponCurrentIndex = 0, subWeaponCurrentIndex = 0;

    WeaponManager weaponManager = Aradite.getInstance().getWeaponManager();
    List<Weapon> mainWeapons = weaponManager.getWeaponsByType(WeaponType.MAIN);
    List<Weapon> subWeapons = weaponManager.getWeaponsByType(WeaponType.SUB);

    private String currentMainWeaponSkin = "default";
    private String currentSubWeaponSkin = "default";

    public AgentSelectUI(Match match, Player player, AgentSelectTask task) {
        super(54, AGENT_SELECT_UI_ID);

        setSelfClosable(false);

        this.match = match;
        this.player = player;
        this.task = task;
        this.packet = task.getSelectPacket(player);
        this.packetPackage = packet.getPacketContents();

        MatchTeam teamManager = match.getMatchTeam();
        UndefinedTeam team = (UndefinedTeam) teamManager.getTeam(TeamRole.UNDEFINED);
        UndefinedTeam.UndefinedTeamType playerTeamType = team.getTeamOf(player);

        this.allies.addAll(team.getPlayerInTeam(playerTeamType));
        this.enemies.addAll(team.getPlayerInTeam(UndefinedTeam.UndefinedTeamType.not(playerTeamType)));

        for (int i = 0; i < allies.size(); i++) {
            Player p = allies.get(i);
            Pair<Integer, Agents> data = Pair.of(allySlots[i], null);
            selectData.put(p.getName(), data);

            setItem(allySlots[i], new MenuItem(new ItemBuilder(Material.PLAYER_HEAD,
                    lang.getString("ui.agent-select.ally-name").replaceAll("%player_name%",
                            p.getName()), lang.getString("ui.agent-select.not-picked"))
                    .setTextureURL(p.getName().equals(player.getName()) ? Icon.NOT_PICKED_SELF.getURL()
                            : Icon.NOT_PICKED_ALLY.getURL())) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });
        }

        for (int i = 0; i < enemies.size(); i++) {
            Player p = enemies.get(i);
            Pair<Integer, Agents> data = Pair.of(enemySlots[i], null);
            selectData.put(p.getName(), data);

            setItem(enemySlots[i], new MenuItem(new ItemBuilder(Material.PLAYER_HEAD,
                    lang.getString("ui.agent-select.enemy-name").replaceAll("%player_name%", p.getName()),
                    lang.getString("ui.agent-select.not-picked")).setTextureURL(Icon.NOT_PICKED_ENEMY.getURL())) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });
        }

        setItem(22, new MenuItem(Material.BARRIER, lang.getString("ui.agent-select.not-your-turn")) {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
            }
        });

    }

    @Override
    public void runnable() {
        int clock = this.task.getClock();

        Icon timeIcon = clock > 0 ? Icon.valueOf(RomanNumerals.toRomanNumeral(clock)) : Icon.ZERO;
        setItem(countdownSlot, new MenuItem(new ItemBuilder(Material.PLAYER_HEAD, "§f§l" + clock, lang.getString("ui.agent-select.phase-description").replaceAll("%phase_description%", this.task.getPhase().getDescription())).setTextureURL(timeIcon.getURL())) {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
            }
        });

        if (task.getPhase() == AgentSelectTask.AgentSelectPhase.FINALIZING) {
            for (int i : arrowInstruction) setItem(i, new MenuItem(Material.AIR));
        }

        if (task.getPhase() == AgentSelectTask.AgentSelectPhase.SELECTING) {
            Player currentTurn = this.task.getCurrentTurn();

            if (!currentTurn.getName().equals(oldName)) {
                currentTurn.playSound(currentTurn.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);

                this.oldName = currentTurn.getName();
                for (int i : arrowInstruction) setItem(i, new MenuItem(Material.AIR));

                boolean isAlly = allies.contains(currentTurn);
                int displaySlot = this.selectData.get(currentTurn.getName()).getKey();
                int instructionArrowSlot = isAlly ? displaySlot + 1 : displaySlot - 1;

                setItem(instructionArrowSlot, new MenuItem(isAlly ? LEFT_ARROW : RIGHT_ARROW) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                    }
                });
            }
        }

        for (Player pLAYER : this.task.getAllPlayers()) {
            Pair<Integer, Agents> selectData = this.selectData.get(pLAYER.getName());
            int slot = selectData.getKey();
            Agents selectedAgent = this.task.getSelectPacket(pLAYER).getSelectedAgentType();
            if (selectedAgent == null) continue;

            try {
                Agent instance = selectedAgent.getWrapper().newInstance();
                setItem(slot, new MenuItem(new ItemBuilder(Material.PLAYER_HEAD, "§f§l" +
                        instance.getDisplayName(), "§r §r §a" + pLAYER.getName()).setTextureURL(instance.getTextures().get(TextureType.SKULL).getData())) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Pair<Integer, Agents> data = this.selectData.get(player.getName());
        boolean hasPickedAgent = data.getValue() != null;

        DataController database = Aradite.getInstance().getDataController();
        PlayerDataStorage dataStorage = database.getUserData(player.getUniqueId());

        if (hasPickedAgent && !updated) {
            this.updated = true;

            for (int i : agentSlots)
                setItem(i, new MenuItem(Material.AIR));

            Agents agentEnum = this.packet.getSelectedAgentType();
            Agent agent = this.packet.getSelectedAgent();

            Texture headTexture = agent.getTextures().get(TextureType.SKULL);

            int masteryPoint = dataStorage.getMastery(agentEnum);

            StringBuilder loreBuilder = new StringBuilder();
            List<String> agentInfo = lang.getListWithPlaceholders("ui.agent-select.agent-info",
                    Placeholder.of("mastery_point", "" + masteryPoint),
                    Placeholder.of("mastery_rank", "" + AgentMasteryRank.getRank(masteryPoint).toString()));
            setItem(this.pickedAgentAvatar, new MenuItem(new ItemBuilder(Material.PLAYER_HEAD)
                    .setDisplayName("§f§l" + agent.getDisplayName()).setLore(agentInfo).setTextureURL(headTexture.getData())) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });

            Weapon currentMainWeapon = mainWeapons.get(mainWeaponCurrentIndex);
            packet.setSelectedWeapon(WeaponType.MAIN, currentMainWeapon);
            packet.getPacketContents().write("main-weapon-skin", currentMainWeaponSkin);
            Weapon currentSubWeapon = subWeapons.get(subWeaponCurrentIndex);
            packet.setSelectedWeapon(WeaponType.SUB, currentSubWeapon);
            packet.getPacketContents().write("sub-weapon-skin", currentSubWeaponSkin);

            setItem(mainWeapon, new MenuItem(new ItemBuilder(currentMainWeapon.toItemStack(currentMainWeaponSkin))
                    .addLoreLine("", lang.getString("ui.agent-select.weapon-select.click-to-change-skin"))) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);

                    List<String> skins = currentMainWeapon.getMeta().getSkins().stream().map(skin -> skin.getSkinID()).collect(Collectors.toList());
                    if (skins.isEmpty()) {
                        player.playSound(player.getEyeLocation(), Sound.ENTITY_BLAZE_DEATH, 1, 1);
                        return;
                    }

                    if (currentMainWeaponSkin.equalsIgnoreCase("default")) {
                        currentMainWeaponSkin = skins.get(0);
                        updated = false;
                    } else {
                        int index = skins.indexOf(currentMainWeaponSkin);
                        if (index >= 0) {
                            int next = index + 1;
                            if (next >= skins.size()) {
                                currentMainWeaponSkin = "default";
                                updated = false;
                                return;
                            }

                            currentMainWeaponSkin = skins.get(next);
                            updated = false;
                        }
                    }
                }
            });

            setItem(mainWeapon + 1, new MenuItem(Material.AIR));

            setItem(mainWeapon - 1, new MenuItem(Material.AIR));

            if (mainWeaponCurrentIndex < mainWeapons.size() - 1) {
                setItem(mainWeapon + 1, new MenuItem(new ItemBuilder(MAIN_WEAPON_NEXT)
                        .addLoreLine(lang.getString("ui.agent-select.weapon-select.click-to-next"),
                                "", lang.getString("ui.agent-select.weapon-select.following-weapon"),
                                mainWeapons.get(mainWeaponCurrentIndex + 1).toItemStack().getItemMeta().getDisplayName())) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                        updated = false;
                        mainWeaponCurrentIndex++;
                        currentMainWeaponSkin = "default";
                    }
                });
            }

            if (mainWeaponCurrentIndex > 0) {
                setItem(mainWeapon - 1, new MenuItem(new ItemBuilder(MAIN_WEAPON_PREVIOUS)
                        .addLoreLine(lang.getString("ui.agent-select.click-to-previous"), "",
                                lang.getString("ui.agent-select.weapon-select.before-weapon"),
                                mainWeapons.get(mainWeaponCurrentIndex - 1).toItemStack().getItemMeta().getDisplayName())) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                        updated = false;
                        mainWeaponCurrentIndex--;
                        currentMainWeaponSkin = "default";
                    }
                });
            }

            setItem(subWeapon, new MenuItem(new ItemBuilder(currentSubWeapon.toItemStack(currentSubWeaponSkin)).

                    addLoreLine("",
                            lang.getString("ui.agent-select.weapon-select.click-to-change-skin"))) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);

                    List<String> skins = currentSubWeapon.getMeta().getSkins().stream().map(skin -> skin.getSkinID())
                            .collect(Collectors.toList());
                    if (skins.isEmpty()) {
                        player.playSound(player.getEyeLocation(), Sound.ENTITY_BLAZE_DEATH, 1, 1);
                        return;
                    }

                    if (currentSubWeaponSkin.equalsIgnoreCase("default")) {
                        currentSubWeaponSkin = skins.get(0);
                        updated = false;
                    } else {
                        int index = skins.indexOf(currentSubWeaponSkin);
                        if (index >= 0) {
                            int next = index + 1;
                            if (next >= skins.size()) {
                                currentSubWeaponSkin = "default";
                                updated = false;
                                return;
                            }

                            currentSubWeaponSkin = skins.get(next);
                            updated = false;
                        }
                    }
                }
            });

            setItem(subWeapon + 1, new MenuItem(Material.AIR));

            setItem(subWeapon - 1, new MenuItem(Material.AIR));

            if (subWeaponCurrentIndex < subWeapons.size() - 1) {
                setItem(subWeapon + 1, new MenuItem(new ItemBuilder(SUB_WEAPON_NEXT).addLoreLine(lang.getString("ui.agent-select.weapon-select.click-to-next"), "", lang.getString("ui.agent-select.weapon-select.following-weapon"), subWeapons.get(subWeaponCurrentIndex + 1).getDisplayName())) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                        updated = false;
                        subWeaponCurrentIndex++;
                        currentSubWeaponSkin = "default";
                    }
                });
            }

            if (subWeaponCurrentIndex > 0) {
                setItem(subWeapon - 1, new MenuItem(new ItemBuilder(SUB_WEAPON_PREVIOUS).addLoreLine(lang.getString("ui.agent-select.click-to-previous"), "", lang.getString("ui.agent-select.weapon-select.before-weapon"), subWeapons.get(subWeaponCurrentIndex - 1).getDisplayName())) {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                        updated = false;
                        subWeaponCurrentIndex--;
                        currentSubWeaponSkin = "default";
                    }
                });
            }
        } else {
            if (!(task.getCurrentTurn() != null && task.getCurrentTurn().getName().equals(player.getName())))
                return;

            int slotSize = agentSlots.length, page = this.currentPage, slotPosition = data.getKey();
            List<Agents> availableAgents = Lists.newArrayList(Agents.values());

            for (int i = page * slotSize; i < (page + 1) * slotSize; i++) {
                int index = page > 0 ? i - page * slotSize : i;
                if (i >= availableAgents.size()) {
                    setItem(agentSlots[index], new MenuItem(Material.AIR));
                    continue;
                }

                Agents agentEnum = availableAgents.get(i);
                Class<?> agentWrapper = agentEnum.getWrapper();
                try {
                    Agent agent = (Agent) agentWrapper.newInstance();
                    Texture headTexture = agent.getTextures().get(TextureType.SKULL);
                    setItem(agentSlots[index], new MenuItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§f§l" + agent.getDisplayName()).setTextureURL(headTexture.getData())) {
                        @Override
                        public void onClick(InventoryClickEvent e) {
                            e.setCancelled(true);
                            selectData.put(player.getName(), Pair.of(slotPosition, agentEnum));
                            player.playSound(player.getEyeLocation(), Sound.BLOCK_BELL_USE, 1, 1);

                            packetPackage.write("agent-type", agentEnum.toString());

                            match.getReport().log("[AGENT_SELECT] " + player.getName() + " has selected " + agentEnum.toString());
                            dataStorage.increaseAgentPickData(agentEnum);

                            task.nextPlayer();
                        }
                    });
                } catch (InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    static enum Icon {
        ZERO("85bd1e613ff32b523ccf9e574cc311b798c2b3a6828f0f71a254c995e6db8e5"),

        I("8d2454e4c67b323d5be953b5b3d54174aa271460374ee28410c5aeae2c11f5"),

        II("b13b778c6e5128024214f859b4fadc7738c7be367ee4b9b8dbad7954cff3a"),

        III("031f66be0950588598feeea7e6c6779355e57cc6de8b91a44391b2e9fd72"),

        IV("95bc42c69846c3da9531ac7dba2b55363f8f9472576e17d423b7a9b81c9151"),

        V("df3f565a88928ee5a9d6843d982d78eae6b41d9077f2a1e526af867d78fb"),

        VI("df4960f57dcc1852f974a4288778b45477678b3c3671e2cfa8205022443b6923"),

        VII("e4fd8cfc44658f6c78c376b2752df3c63610a5aed194f2eca3e6736d47ce72"),

        VIII("7acc81bd5a59a1862a2c01be7412231e15ce057d458fbc18435b9385593f5"),

        IX("85bdbddbb81ae7a894b6df0a5f8e21c22fbd065dc8211b41737e0a1d0c2cfc6"),

        X("d88e1270728442a416df885e811812b874fe76bd2926c1c497273d52f227bf7"),

        XI("9824398f62be771ec87908bcce4e9b7aa93fd62d13d4c522fd431aab7d0b2"),

        XII("1b3fda527b5b171cbbcc4899bd89852245843e3b41bed8723a73ac32a982ad"),

        XIII("70d19fa79817ba54aed53fa1f8898d3236103a838d579a52858bddb2f9fe4d4a"),

        XIV("b76d7748f989b5d9bfa8ce261beb9010862f15e4c9941a73393c20fe40c2d0bb"),

        XV("42fca7ea66c99724718b40966d2d19f4aec52523b7cfe0ef32834f742b2549"),

        XVI("e9f329a6e6c6f262c5b3e3de831405290532e9d3b8c96cae64abf5acd25fce"),

        XVII("6dfb56152348ff3fac57853f429356884aaa095be2af1918daa48e12a5b544"),

        XVIII("929791494794c1b266af19b19ac2ed136d7e60a31124259ba74c8dd9e325e278"),

        XIX("70a6533ed2dc5ea7864c9ddcf14c20fcd7f7922553f4d66541b3b9c32a3"),

        XX("39976799f4698f7f78b45b874efe1a10cf8d9284cf2f3a4e5f98ec27afba7fc8"),

        NOT_PICKED_ALLY("c35ba393b8610b63ebee4c13c8358bc6c94a9dedc8e4d7d36b922257e65e8"),

        NOT_PICKED_SELF("718079d5847416abf44e8c2fec2ccd44f08d736ca8e51f95a436d85f643fbc"),

        NOT_PICKED_ENEMY("4fd5bde994e0a647af1823681a613c2bfc3d9736f889dbf8c3bbba5a13f8ed");

        private String url;

        private Icon(String url) {
            this.url = url;
        }

        public String getURL() {
            return url;
        }

        static {
            Inventory inventory = Bukkit.createInventory(null, 54, "abc");
            for (Icon icon : values()) {
                ItemStack item = new ItemBuilder(Material.PLAYER_HEAD).setTextureURL(icon.getURL()).create();
                inventory.setItem(0, item);
            }
        }
    }

}
