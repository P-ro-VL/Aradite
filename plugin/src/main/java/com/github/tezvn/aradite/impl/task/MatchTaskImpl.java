package com.github.tezvn.aradite.impl.task;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.mechanic.Mechanic;
import com.github.tezvn.aradite.api.task.MatchTask;
import com.github.tezvn.aradite.impl.AraditeImpl;
import com.github.tezvn.aradite.impl.task.type.MechanicManagementTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pdx.mantlecore.java.collection.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An arena needs some runnables to do special counting-relating tasks.
 * Normally, those runnables will be executed every one seconds and only take
 * effect when there's at least 1 player in the arena.<br>
 * They will be automatically paused when the arena is empty.
 * 
 * @author phongphong28
 */
public class MatchTaskImpl implements MatchTask {

	public static ExecutorService singleAsyncTasks = Executors.newFixedThreadPool(3);
	private Queue<Runnable> singleSyncTasks = new LinkedList<>();

	private List<BukkitTask> timerTasks = Lists.newArrayList();

	private AtomicBoolean preventAdaptingAsyncTask = new AtomicBoolean(false);

	private MechanicManagementTask mechanicManagementTask;

	/**
	 * Run the {@link MechanicManagementTask}. If the task has been run before, do nothing.
	 */
	public void runMechanicManagement(Match match, Mechanic currentMechanic){
		if(mechanicManagementTask != null) return;
		this.mechanicManagementTask = new MechanicManagementTask(match, currentMechanic);
		this.mechanicManagementTask.start();
	}

	/**
	 * Add a task to queue to be processed.
	 * 
	 * @param task
	 *            Task to be executed
	 * @param async
	 *            {@code true} if the task will be executed asynchronously.
	 */
	public void addSingleTask(Runnable task, boolean async) {
		if (async && preventAdaptingAsyncTask.get() == false) {
			this.singleAsyncTasks.execute(task);
		} else {
			this.singleSyncTasks.add(task);
		}
	}

	/**
	 * Add a task to queue to be processed synchronously.
	 * 
	 * @param task
	 *            Task to be executed
	 */
	public void addSingleTask(Runnable task) {
		addSingleTask(task, false);
	}

	/**
	 * Add a timer task to queue to be processed.
	 * 
	 * @param task
	 *            Task to be executed.
	 * @param async
	 *            {@code true} if the task will be runned asynchronously
	 * @param time
	 *            The amount of times that the task will run (in seconds).
	 * @param delay
	 *            The amount of time that the task will wait before being reexecuted
	 *            (in ticks).
	 */
	public void addTimerTask(Runnable task, boolean async, int time, int delay, Runnable callback) {
		BukkitRunnable runnable = new BukkitRunnable() {
			int t = 0;

			@Override
			public void run() {
				t += delay;
				if (t / 20 >= time) {
					this.cancel();
					if (callback != null)
						callback.run();
					return;
				}

				task.run();
			}
		};

		BukkitTask bukkitTask = null;
		if (async) {
			bukkitTask = runnable.runTaskTimerAsynchronously(AraditeImpl.getInstance(), delay, delay);
		} else {
			bukkitTask = runnable.runTaskTimer(AraditeImpl.getInstance(), delay, delay);
		}

		getTimerTaskExecutor().add(bukkitTask);
	}

	/**
	 * Add a {@link BukkitTask} to the task queue.
	 * @param task New task
	 */
	public void addSimpleTask(BukkitTask task) {
		this.timerTasks.add(task);
	}
	
	/**
	 * Add a timer task to queue to be processed.
	 * 
	 * @param task
	 *            Task to be executed.
	 * @param async
	 *            {@code true} if the task will be runned asynchronously
	 * @param time
	 *            The time that the task will run (in seconds).
	 * @param delay
	 *            The amount of time that the task will wait before being reexecuted
	 *            (in ticks).
	 */
	public void addTimerTask(Runnable task, boolean async, int time, int delay) {
		addTimerTask(task, async, time, delay, null);
	}

	/**
	 * Instantly shut down all the arena task.<br>
	 * This method will be automatically called when an arena is reach its end.
	 */
	public void shutdown() {
		preventAdaptingAsyncTask.set(true);
		getAsyncTaskExecutor().shutdownNow();
		getSyncTaskExecutor().clear();

		getTimerTaskExecutor().forEach(task -> {
			if (task.isCancelled())
				task.cancel();
		});
		getTimerTaskExecutor().clear();
	}

	public ExecutorService getAsyncTaskExecutor() {
		return this.singleAsyncTasks;
	}

	public Queue<Runnable> getSyncTaskExecutor() {
		return this.singleSyncTasks;
	}

	public List<BukkitTask> getTimerTaskExecutor() {
		return timerTasks;
	}

	/**
	 * Refresh the arena task manager.<br>
	 * This process will be called before any other activities is done.
	 */
	public void refresh() {
		this.singleAsyncTasks = Executors.newFixedThreadPool(3);
		preventAdaptingAsyncTask.set(false);
	}

}
