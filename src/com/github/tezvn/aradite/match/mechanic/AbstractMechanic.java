package com.github.tezvn.aradite.match.mechanic;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;
import com.github.tezvn.aradite.match.Match;
import com.github.tezvn.aradite.task.AraditeTask;

import java.util.concurrent.TimeUnit;

public abstract class AbstractMechanic implements Mechanic {

    public static final Language lang = Aradite.getInstance().getLanguage();

    private Match match;
    private AraditeTask task;
    private int index = 1;
    private long startTime;
    private boolean isCompleted = false;

    public AbstractMechanic(Match match) {
        this.match = match;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void start() {
        match.getReport().log("[" + getID() + "-" + getIndex() + "] Round mechanic is going to start.");
        this.startTime = System.currentTimeMillis();
        onStart();
        task = getTask();
        task.start();
    }

    @Override
    public void finish() {
        match.getReport().log("[" + getID() + "-" + getIndex() + "] Round mechanic has finished. " +
                "(Total time: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) + " seconds)");
        task.cancel();
        onFinish();
        this.isCompleted = true;
    }

    @Override
    public Match getMatch() {
        return this.match;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }
}
