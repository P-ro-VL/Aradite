package com.github.tezvn.aradite.task;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.State;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.bukkit.Bukkit;

/**
 * A timer task that run asynchronously.
 *
 * @author phongphong28
 */
public abstract class AsyncTimerTask extends TimerTask {

    private Thread thread;

    private boolean isPaused = false;

    private String threadID;

    public AsyncTimerTask(TimeUnit unit, int time, String threadID) {
        super(unit, time);
        this.thread = new Thread(() -> {
            while (!isPaused()) {
                run();
                try {
                    Thread.sleep(unit.toMillis(time));
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        this.thread.setName(threadID);
        this.threadID = threadID;
    }

    public String getThreadID() {
        return threadID;
    }

    @Override
    public void start() {
        if (isPaused()) {
            isPaused = false;
            thread.notify();
            return;
        }

        onExecute();
        thread.start();
    }

    @Override
    public void pause() {
        try {
            thread.wait();
            isPaused = true;
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe("[ARADITE] Cannot pause an async timer task !");
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        this.isPaused = true;
        thread.interrupt();
        System.gc();
    }

    @Override
    public void wait(int tick) {
        try {
            pause();
            Timer timer = new Timer((int) ((tick / 20) * 0.001), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    start();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[ARADITE] Cannot make an async timer task wait for " + tick + " ticks !");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean isRunning() {
        return thread.isAlive();
    }

    @Override
    public boolean isCancelled() {
        return thread.isInterrupted();
    }

    @Override
    public boolean isWaiting() {
        return thread.getState() == State.WAITING || thread.getState() == State.TIMED_WAITING;
    }

}
