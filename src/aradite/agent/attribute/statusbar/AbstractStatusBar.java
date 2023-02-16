package aradite.agent.attribute.statusbar;

import aradite.Aradite;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractStatusBar implements StatusBar {

    private final Player owner;
    private BossBar bossBar;

    private BukkitTask updateTask;
    private boolean isPaused = false;

    public AbstractStatusBar(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void show() {
        bossBar.setVisible(true);
    }

    @Override
    public void hide() {
        bossBar.setVisible(false);
    }

    @Override
    public void updatePercentage(double percentage) {
        bossBar.setProgress(percentage);
    }

    @Override
    public void updateMessage(String message) {
        bossBar.setTitle(message);
    }

    @Override
    public void start() {
        this.bossBar = Bukkit.createBossBar("", getBarColor(), getBarStyle(), BarFlag.PLAY_BOSS_MUSIC);
        this.bossBar.addPlayer(getOwner());
        show();

        this.updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(isPaused) return;
                update();
            }
        }.runTaskTimerAsynchronously(Aradite.getInstance(), 1, 1);
    }

    @Override
    public void kill() {
        updateTask.cancel();
        hide();
        bossBar.removeAll();
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        if(isPaused) isPaused = false;
    }
}
