package com.github.tezvn.aradite.impl.match.mechanic;

import com.github.tezvn.aradite.api.language.Language;
import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.mechanic.Mechanic;
import com.github.tezvn.aradite.api.task.AraditeTask;
import com.github.tezvn.aradite.impl.AraditeImpl;

import java.util.concurrent.TimeUnit;

public abstract class AbstractMechanic implements Mechanic {

    public static final Language lang = AraditeImpl.getInstance().getLanguage();

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
