package com.github.tezvn.aradite.api.match.mechanic;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.task.AraditeTask;

/**
 * Each mechanic represents a type of gameplay in a match.
 */
public interface Mechanic {

    /**
     * Return the index of the mechanic.<br>
     * Index is usually the ordinal number of current round.
     * We use this method to concatenate with {@link #getID()} to fill in the report.<br>
     * The index often starts with 1.
     */
    int getIndex();

    /**
     * Change the index of the mechanic.
     *
     * @param index The new index.
     */
    void setIndex(int index);

    /**
     * Return the ID of the mechanic.
     */
    String getID();

    /**
     * Start one round of the mechanic.
     */
    void start();

    /**
     * Finish one round of the mechanic.
     */
    void finish();

    /**
     * Mechanic actions that will be repeatedly performed.
     */
    AraditeTask getTask();

    /**
     * Actions that will be done when the mechanic starts.
     */
    void onStart();

    /**
     * Actions that will be performed when the mechanic finishes.
     */
    void onFinish();

    /**
     * The match that the mechanic belongs to.
     */
    Match getMatch();

    /**
     * Return {@code true} if the mechanic has totally finished. (Completed all rounds).
     */
    boolean isCompleted();

    /**
     * Change the completion state of the mechanic.
     *
     * @param completed Completion state.
     */
    void setCompleted(boolean completed);

    /**
     * Return the type of the mechanic.
     */
    MechanicType getMechanicType();

}
