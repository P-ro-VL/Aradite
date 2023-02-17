package com.github.tezvn.aradite.api.agent.attribute.statusbar;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

/**
 * The boss bar that show some statuses.
 */
public interface StatusBar {

    /**
     * Return the type of the status bar.
     */
    public StatusBarType getBarType();

    /**
     * Return the color of the bar.
     */
    public BarColor getBarColor();

    /**
     * Return the style of the bar
     */
    public BarStyle getBarStyle();

    /**
     * Return the owner of the bar.
     */
    public Player getOwner();

    /**
     * Update the bar.
     */
    public void update();

    /**
     * Set the progress of the bar. The value must be between 0.0 and 1.0;
     *
     * @param percentage The progress
     */
    public void updatePercentage(double percentage);

    /**
     * Change the bar displaying title.
     *
     * @param message The new title
     */
    public void updateMessage(String message);

    /**
     * Show the bar
     */
    public void show();

    /**
     * Hide the bar
     */
    public void hide();

    /**
     * Start a task that run every tick to update the bar.
     */
    public void start();

    /**
     * Stop the update task and hide the bar.
     */
    public void kill();

    /**
     * Temporarily pause the update task
     */
    public void pause();

    /**
     * Continue the update task after being paused. ({@link #pause()}.
     */
    public void resume();

}
