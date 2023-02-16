package aradite.listener.ingame;

import aradite.Aradite;
import aradite.agent.skill.Skill;
import aradite.data.DataController;
import aradite.data.EnumDataKey;
import aradite.data.Statistic;
import aradite.data.global.PlayerDataStorage;
import aradite.data.packet.PacketType;
import aradite.data.packet.type.PlayerInGameLastDamagePacket;
import aradite.data.packet.type.PlayerInGameMVPPacket;
import aradite.event.AgentDeathEvent;
import aradite.language.Language;
import aradite.match.Match;
import aradite.team.MatchTeam;
import aradite.weapon.Weapon;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pdx.mantlecore.task.TaskQueue;

public class AgentDeathModule implements Listener {

    private Language lang = Aradite.getInstance().getLanguage();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAgentDeath(AgentDeathEvent e) {
        Match match = e.getMatch();

        PlayerInGameLastDamagePacket.DeathReason deathReason = e.getDeathReason();
        Player killer = e.getKiller();
        Player player = e.getTarget();
        Weapon weapon = e.getWeapon();
        String skillName = e.getSkill();

        MatchTeam matchTeam = match.getMatchTeam();

        String broadcast = lang.getString("death-reason." + deathReason.toString())
                .replaceAll("%dmger_team_color%", killer == null ? "§4" : matchTeam.getTeamColor(killer))
                .replaceAll("%dmger%", killer == null ? "Unknown" : killer.getName())
                .replaceAll("%target_team_color%", matchTeam.getTeamColor(player))
                .replaceAll("%target%", player.getName())
                .replaceAll("%weapon%", weapon == null ? "Unknown" : weapon.getDisplayName())
                .replaceAll("%skill%", skillName == null ? "Unknown" : skillName);

        matchTeam.broadcast(MatchTeam.BroadcastType.NORMAL_CHAT, broadcast);

        player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 2);

        DataController dataController = Aradite.getInstance().getDataController();

        PlayerDataStorage targetDataStorage = dataController.getUserData(player.getUniqueId());
        targetDataStorage.increase(Statistic.DEATH, 1);

        PlayerInGameMVPPacket TARGETmvpPacket = (PlayerInGameMVPPacket)
                match.retrieveProtocol(player).getPacket(PacketType.INGAME_MVP);
        TARGETmvpPacket.increaseByOne(Statistic.DEATH);

        if (killer != null) {
            PlayerDataStorage killerDataStorage = dataController.getUserData(killer.getUniqueId());
            killerDataStorage.increase(Statistic.KILL, 1);

            PlayerInGameMVPPacket KILLERmvpPacket = match.retrieveProtocol(killer)
                    .getPacket(PlayerInGameMVPPacket.class);
            KILLERmvpPacket.increaseByOne(Statistic.KILL);
            KILLERmvpPacket.addMVPPoint(PlayerInGameMVPPacket.MVPStatistics.KILL, 1);
        }

        TaskQueue.runSync(Aradite.getInstance(), () -> {
            player.setGameMode(GameMode.SPECTATOR);
        });
    }
}
