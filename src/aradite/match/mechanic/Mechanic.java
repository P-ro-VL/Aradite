package aradite.match.mechanic;

import aradite.match.Match;
import aradite.task.AraditeTask;

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
    public int getIndex();

    /**
     * Change the index of the mechanic.
     * @param index The new index.
     */
    public void setIndex(int index);

    /**
     * Return the ID of the mechanic.
     */
    public String getID();

    /**
     * Start one round of the mechanic.
     */
    public void start();

    /**
     * Finish one round of the mechanic.
     */
    public void finish();

    /**
     * Mechanic actions that will be repeatedly performed.
     */
    public AraditeTask getTask();

    /**
     * Actions that will be done when the mechanic starts.
     */
    public void onStart();

    /**
     * Actions that will be performed when the mechanic finishes.
     */
    public void onFinish();

    /**
     * The match that the mechanic belongs to.
     */
    public Match getMatch();

    /**
     * Return {@code true} if the mechanic has totally finished. (Completed all rounds).
     */
    public boolean isCompleted();

    /**
     * Change the completion state of the mechanic.
     * @param completed Completion state.
     */
    public void setCompleted(boolean completed);

    /**
     * Return the type of the mechanic.
     */
    public MechanicType getMechanicType();

}
