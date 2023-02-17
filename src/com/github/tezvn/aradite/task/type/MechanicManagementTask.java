package com.github.tezvn.aradite.task.type;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.agent.attribute.AttributeType;
import com.github.tezvn.aradite.agent.attribute.statusbar.StatusBar;
import com.github.tezvn.aradite.data.packet.PacketType;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameAttributePacket;
import com.github.tezvn.aradite.data.packet.type.PlayerInGameLastDamagePacket;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.Match;
import com.github.tezvn.aradite.match.MatchFlag;
import com.github.tezvn.aradite.match.MatchScore;
import com.github.tezvn.aradite.match.mechanic.Mechanic;
import com.github.tezvn.aradite.match.mechanic.MechanicType;
import com.github.tezvn.aradite.task.AsyncTimerTask;
import com.github.tezvn.aradite.team.MatchTeam;
import com.github.tezvn.aradite.team.type.UndefinedTeam;
import com.destroystokyo.paper.Title;
import io.netty.handler.logging.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import pdx.mantlecore.task.TaskQueue;

import java.util.concurrent.TimeUnit;

public class MechanicManagementTask extends AsyncTimerTask {

    private static final Language lang = Aradite.getInstance().getLanguage();
    private Mechanic mechanic;
    private Match match;
    private boolean switchingToNewPhase = false;
    private boolean switchingToNewMechanic = false;

    private int clock = 0;

    public MechanicManagementTask(Match match, Mechanic mechanic) {
        super(TimeUnit.SECONDS, 1, "mechanic-management");

        this.match = match;
        this.mechanic = mechanic;
    }

    @Override
    public void onExecute() {
        match.getReport().log("[MECHANIC_MANAGEMENT_TASK] Started mechanic management task for "
                + mechanic.getID() + "-" + mechanic.getIndex() + "...");
    }

    @Override
    public void run() {
        try {
            MatchTeam matchTeam = match.getMatchTeam();

            if (switchingToNewMechanic || switchingToNewPhase) {
                this.clock--;

                String path = switchingToNewMechanic ? "mechanic" : "phase";

                if (clock == 0) {
                    match.getStatusBars().values().forEach(StatusBar::pause);

                    matchTeam.getAllPlayers().forEach(player -> {
                        TaskQueue.runSync(Aradite.getInstance(), () -> {
                            player.setGameMode(GameMode.ADVENTURE);
                        });

                        PlayerInGameLastDamagePacket lastDamagePacket = (PlayerInGameLastDamagePacket)
                                match.retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);
                        lastDamagePacket.setDead(false);

                        PlayerInGameAttributePacket attributePacket = (PlayerInGameAttributePacket)
                                match.retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                        attributePacket.setAttribute(AttributeType.CURRENT_HEALTH,
                                attributePacket.getAttribute(AttributeType.MAX_HEALTH));
                    });

                    if (switchingToNewPhase) {
                        MechanicType currentType = this.mechanic.getMechanicType();
                        match.runMechanic(currentType, match.getCurrentMechanic().getIndex() + 1);
                    } else {
                        MatchScore matchScore = match.getMatchScore();
                        int teamAScore = matchScore.getWinPhase(UndefinedTeam.UndefinedTeamType.A);
                        int teamBScore = matchScore.getWinPhase(UndefinedTeam.UndefinedTeamType.B);

                        UndefinedTeam.UndefinedTeamType winTeam = teamAScore > teamBScore ?
                                UndefinedTeam.UndefinedTeamType.A : UndefinedTeam.UndefinedTeamType.B;

                        matchTeam.defineAttacker(winTeam);
                        match.runMechanic(MechanicType.BOMB_CART, 1);
                    }
                    this.mechanic = match.getCurrentMechanic();
                    this.switchingToNewMechanic = false;
                    this.switchingToNewPhase = false;

                    match.getStatusBars().values().forEach(StatusBar::resume);
                    return;
                }

                matchTeam.broadcastTitle(new Title(lang.getString("match.ingame.changing-" + path +
                        "-title.title").replaceAll("%round%", "" + (match.getCurrentMechanic().getIndex()
                        + 1)),
                        lang.getString("match.ingame.changing-" + path + "-title.sub-title")
                                .replaceAll("%countdown%", "" + clock), 20, 60, 20));
                return;
            }

            if (mechanic.isCompleted()) {
                int lastIndex = mechanic.getIndex();
                match.getReport().log("[MECHANIC_MANAGEMENT_TASK] The mechanic '"
                        + mechanic.getID() + "-" + mechanic.getIndex() + "' has finished.");

                if (lastIndex == 3) {
                    if (mechanic.getMechanicType() == MechanicType.CAPTURE) {
                        this.switchingToNewMechanic = true;
                        this.clock = 10;
                        this.match.setupFlag(MatchFlag.ALL_PLAYERS_INVUNERABLE, true);
                    } else {
                        match.finish();
                        cancel();
                    }
                } else {
                    this.switchingToNewPhase = true;
                    this.clock = 10;
                    this.match.setupFlag(MatchFlag.ALL_PLAYERS_INVUNERABLE, true);
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("An error has occured when running the mechanic management task '"
                    + mechanic.getID() + "' !\nThe match has been cancel !");
            cancel();
            match.getReport().log("There has been an exception during running the mechanic management task !", LogLevel.ERROR);
            match.getReport().log("Exception Detail :", LogLevel.ERROR);
            match.getReport().log("- Exception Message : " + ex.getMessage(), LogLevel.ERROR);
            match.getReport().log("- Stack Trace : " + getExceptionMessage(ex), LogLevel.ERROR);
        }
    }

    public static String getExceptionMessage(Exception ex) {
        String result = "";
        StackTraceElement[] stes = ex.getStackTrace();
        for (int i = 0; i < stes.length; i++) {
            result = result + stes[i].getClassName() + "." + stes[i].getMethodName() + "  " + stes[i].getLineNumber()
                    + "line" + "\r\n";
        }
        return result;
    }

}
