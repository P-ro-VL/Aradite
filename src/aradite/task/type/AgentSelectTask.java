package aradite.task.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import aradite.agent.Agent;
import aradite.agent.attribute.Attribute;
import aradite.agent.attribute.AttributeType;
import aradite.agent.attribute.Attributes;
import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameAttributePacket;
import aradite.data.packet.type.PlayerInGameData;
import aradite.data.packet.type.PlayerInGameSkillLevelPacket;
import aradite.data.packet.type.PlayerPreGameSelectPacket;
import aradite.language.Language;
import aradite.match.mechanic.MechanicType;
import aradite.weapon.WeaponType;
import com.destroystokyo.paper.ClientOption;
import com.google.common.collect.Maps;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import aradite.Aradite;
import aradite.match.Match;
import aradite.task.AsyncTimerTask;
import aradite.team.MatchTeam;
import aradite.team.TeamRole;
import aradite.team.type.UndefinedTeam;
import aradite.ui.agentselect.AgentSelectUI;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pdx.mantlecore.menu.Menu;
import pdx.mantlecore.task.CountdownBossbar;
import pdx.mantlecore.task.TaskQueue;

public class AgentSelectTask extends AsyncTimerTask {

    private final int PREPARING_TIME = 5, SELECTING_TIME = 20;

    private Match match;
    private Queue<Player> players = Lists.newLinkedList();
    private Player currentTurn;
    private AgentSelectPhase phase;
    private Map<Player, PlayerPreGameSelectPacket> selectDataMap = Maps.newHashMap();

    private int clock = 0;
    private final Language lang = Aradite.getInstance().getLanguage();

    public AgentSelectTask(Match match) {
        super(TimeUnit.SECONDS, 1, "agent-select-" + match.getUniqueID());
        this.match = match;
        this.phase = AgentSelectPhase.PREPARING;
        this.clock = PREPARING_TIME;

        MatchTeam teamManager = match.getMatchTeam();
        UndefinedTeam team = (UndefinedTeam) teamManager.getTeam(TeamRole.UNDEFINED);
        this.players.addAll(team.getMembers());
    }

    @Override
    public void onExecute() {
        this.players.forEach(player -> {
            PlayerPreGameSelectPacket data = new PlayerPreGameSelectPacket(player);
            this.selectDataMap.put(player, data);

            Menu.open(player, new AgentSelectUI(match, player, this));
        });
    }

    @Override
    public void run() {
        clock--;
        if (clock == 0) {
            if (this.phase == AgentSelectPhase.FINALIZING) {
                setPhase(AgentSelectPhase.FINISHED);
                this.cancel();
                this.match.getReport().log("[AGENT_SELECT] Load up the PlayerPreGameSelectPacket ...");
                this.selectDataMap.entrySet().forEach(entry -> {
                    Player player = entry.getKey();
                    PlayerPreGameSelectPacket packet = entry.getValue();
                    PlayerInGameData data = match.retrieveProtocol(player);
                    data.registerPacket(PacketType.PREGAME_SELECT, packet);

                    match.getMatchTeam().setSelectedAgents(player, packet.getSelectedAgent());

                    PlayerInGameSkillLevelPacket skillLevelPacket = new PlayerInGameSkillLevelPacket(player,
                            packet.getSelectedAgentType());
                    data.registerPacket(PacketType.INGAME_SKILL_LEVEL, skillLevelPacket);

                    TaskQueue.runSync(Aradite.getInstance(), () -> {
                        player.closeInventory();
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8 * 20, 2555,
                                false, false, false));
                    });

                    String mainSkin = packet.getPacketContents().getMapData().get("main-weapon-skin");
                    String subSkin = packet.getPacketContents().getMapData().get("sub-weapon-skin");

                    player.getInventory().setItem(7, packet.getSelectedWeapon(WeaponType.MAIN).toItemStack(mainSkin));
                    player.getInventory().setItem(8, packet.getSelectedWeapon(WeaponType.SUB).toItemStack(subSkin));

                    packet.getSelectedAgent().bindSkill(player);
                });
                this.match.getReport().log("[AGENT_SELECT] Load up the agents' default and additional attributes ...");
                loadAttributes();
                this.match.getReport().log("[AGENT_SELECT] Agent Select phase has finished. " +
                        "The game will be started soon ...");

                match.getReport().log("[INITIALIZING] Setting up status bars.");
                match.setupStatusBars();

                CountdownBossbar bossbar = new CountdownBossbar(Aradite.getInstance(), 7,
                        lang.getString("match.game-start-soon"), BarColor.YELLOW, BarStyle.SEGMENTED_10);
                bossbar.showBossbar(this.getAllPlayers(), new CountdownBossbar.Callback() {
                    @Override
                    public void callback(Player... players) {
                        match.runMechanic(MechanicType.CAPTURE, 1);
                    }
                });
                return;
            }

            nextPlayer();
        }
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public int getClock() {
        return clock;
    }

    public boolean nextPlayer() {
        if (this.players.isEmpty()) {
            setPhase(AgentSelectPhase.FINALIZING);
            this.clock = 15;
            return false;
        }
        this.clock = SELECTING_TIME;
        this.phase = AgentSelectPhase.SELECTING;
        final Player player = this.players.poll();
        this.currentTurn = player;
        System.out.println("Đến lượt " + currentTurn + " chọn - còn lại " + this.players.stream().map(Player::getName)
                .collect(Collectors.toList()).toString());
        return true;
    }

    public AgentSelectPhase getPhase() {
        return phase;
    }

    public void setPhase(AgentSelectPhase phase) {
        this.phase = phase;
    }

    public PlayerPreGameSelectPacket getSelectPacket(Player player) {
        return this.selectDataMap.get(player);
    }

    public Collection<Player> getAllPlayers() {
        return this.selectDataMap.keySet();
    }

    /**
     * Once the agent selecting phase has finished, we need to load both the default and additional attributes
     * of the agent player selected in the {@link aradite.data.packet.type.PlayerInGameAttributePacket}.
     */
    private void loadAttributes() {
        this.selectDataMap.values().forEach(packet -> {
            Player player = packet.getPacketOwner();

            PlayerInGameAttributePacket attributePacket = new PlayerInGameAttributePacket(player);
            attributePacket.setAttributePack(Attributes.DEFAULT_BASE_ATTRIBUTE);
            match.retrieveProtocol(player).registerPacket(PacketType.INGAME_PLAYER_ATTRIBUTE, attributePacket);
        });
    }

    public static enum AgentSelectPhase {
        PREPARING(), SELECTING, FINALIZING, FINISHED;

        public String getDescription() {
            return Aradite.getInstance().getLanguage().getString("ui.agent-select.phase." + this.toString().toLowerCase());
        }
    }

}
