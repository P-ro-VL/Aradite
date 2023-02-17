package com.github.tezvn.aradite.api.task;

public interface AraditeTask {

	/**
	 * Start the task.
	 */
	public void start();

	/**
	 * Pause the task. To continue task, call {@link #start()} once again.
	 */
	public void pause();

	/**
	 * Permanently stop the task.
	 */
	public void cancel();

	/**
	 * Make the task to wait a specific time (in tick) before continuing to run.
	 * 
	 * @param tick
	 *            Time in tick for the task to wait.
	 */
	public void wait(int tick);

	/**
	 * Return {@code true} if the task has been paused before.
	 */
	public boolean isPaused();

	/**
	 * Return {@code true} if the task is running normally.
	 */
	public boolean isRunning();

	/**
	 * Return {@code true} if {@link #cancel()} has been called before.
	 */
	public boolean isCancelled();

	/**
	 * Return {@code true} if the task is in waiting mode.
	 */
	public boolean isWaiting();

	/**
	 * Actions that will be performed when the task is executed.
	 */
	public void onExecute();

}
