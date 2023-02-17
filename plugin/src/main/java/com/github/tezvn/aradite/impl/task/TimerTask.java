package com.github.tezvn.aradite.impl.task;

import com.github.tezvn.aradite.api.task.AraditeTask;

import java.util.concurrent.TimeUnit;

/**
 * A task repeats every specific time.
 */
public abstract class TimerTask implements AraditeTask {

	private TimeUnit unit;

	private int time;

	/**
	 * Setting the specific type of time and its amount for the timer task to loop.
	 * 
	 * @param unit
	 *            Time unit
	 * @param time
	 *            Amount
	 */
	public TimerTask(TimeUnit unit, int time) {
		this.unit = unit;
		this.time = time;
	}

	/**
	 * Return the unit of time the task loop.
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * Return the every amount of time that the task will loop.
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Actions that will be performed repeatedly.
	 */
	public abstract void run();

}
