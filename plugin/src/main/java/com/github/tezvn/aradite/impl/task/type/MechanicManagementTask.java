package com.github.tezvn.aradite.impl.task.type;

import com.github.tezvn.aradite.api.agent.attribute.AttributeType;
import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBar;
import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.MatchFlag;
import com.github.tezvn.aradite.api.match.MatchScore;
import com.github.tezvn.aradite.api.match.mechanic.Mechanic;
import com.github.tezvn.aradite.api.match.mechanic.MechanicType;
import com.github.tezvn.aradite.api.team.MatchTeam;
import com.github.tezvn.aradite.api.team.type.UndefinedTeam;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.api.packet.PacketType;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameAttributePacketImpl;
import com.github.tezvn.aradite.impl.data.packet.type.PlayerInGameLastDamagePacketImpl;
import com.github.tezvn.aradite.impl.match.MatchScoreImpl;
import com.github.tezvn.aradite.impl.task.AsyncTimerTask;
import com.github.tezvn.aradite.impl.team.MatchTeamImpl;
import io.netty.handler.logging.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import pdx.mantlecore.task.TaskQueue;

import java.util.concurrent.TimeUnit;

public class MechanicManagementTask extends AsyncTimerTask {

    private static final Language lang = AraditeImpl.getInstance().getLanguage();
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
                        TaskQueue.runSync(AraditeImpl.getInstance(), () -> {
                            player.setGameMode(GameMode.ADVENTURE);
                        });

                        PlayerInGameLastDamagePacketImpl lastDamagePacket = (PlayerInGameLastDamagePacketImpl)
                                match.retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_LAST_DAMAGE);
                        lastDamagePacket.setDead(false);

                        PlayerInGameAttributePacketImpl attributePacket = (PlayerInGameAttributePacketImpl)
                                match.retrieveProtocol(player).getPacket(PacketType.INGAME_PLAYER_ATTRIBUTE);
                        attributePacket.setAttribute(AttributeType.CURRENT_HEALTH,
                                attributePacket.getAttribute(AttributeType.MAX_HEALTH));
                    });

                    if (switchingToNewPhase) {
                        MechanicType currentType = this.mechanic.getMechanicType();
                        match.runMechanic(currentType, match.getCurrentMechanic().getIndex() + 1);
                    } else {
                        MatchScore matchScore = match.getMatchScore();
                        int teamAScore = matchScore.getWinPhase(UndefinedTeam.Type.A);
                        int teamBScore = matchScore.getWinPhase(UndefinedTeam.Type.B);

                        UndefinedTeam.Type winTeam = teamAScore > teamBScore ?
                                UndefinedTeam.Type.A : UndefinedTeam.Type.B;

                        matchTeam.defineAttacker(winTeam);
                        match.runMechanic(MechanicType.BOMB_CART, 1);
                    }
                    this.mechanic = match.getCurrentMechanic();
                    this.switchingToNewMechanic = false;
                    this.switchingToNewPhase = false;

                    match.getStatusBars().values().forEach(StatusBar::resume);
                    return;
                }

                matchTeam.broadcastTitle(lang.getString("match.ingame.changing-" + path +
                        "-title.title").replaceAll("%round%", "" + (match.getCurrentMechanic().getIndex()
                        + 1)),
                        lang.getString("match.ingame.changing-" + path + "-title.sub-title")
                                .replaceAll("%countdown%", "" + clock), 20, 60, 20);
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
