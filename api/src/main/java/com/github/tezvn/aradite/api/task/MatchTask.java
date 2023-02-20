package com.github.tezvn.aradite.api.task;

import com.github.tezvn.aradite.api.match.Match;
import com.github.tezvn.aradite.api.match.mechanic.Mechanic;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface MatchTask {

    /**
     * Run the {@link MechanicManagementTask}. If the task has been run before, do nothing.
     */
    void runMechanicManagement(Match match, Mechanic currentMechanic);

    /**
     * Add a task to queue to be processed.
     *
     * @param task
     *            Task to be executed
     * @param async
     *            {@code true} if the task will be executed asynchronously.
     */
    void addSingleTask(Runnable task, boolean async);

    /**
     * Add a task to queue to be processed synchronously.
     *
     * @param task
     *            Task to be executed
     */
    void addSingleTask(Runnable task);

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
    void addTimerTask(Runnable task, boolean async, int time, int delay, Runnable callback);

    /**
     * Add a {@link BukkitTask} to the task queue.
     * @param task New task
     */
    void addSimpleTask(BukkitTask task);

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
    void addTimerTask(Runnable task, boolean async, int time, int delay);

    /**
     * Instantly shut down all the arena task.<br>
     * This method will be automatically called when an arena is reach its end.
     */
    void shutdown();

    ExecutorService getAsyncTaskExecutor();

    Queue<Runnable> getSyncTaskExecutor();

    List<BukkitTask> getTimerTaskExecutor();

    /**
     * Refresh the arena task manager.<br>
     * This process will be called before any other activities is done.
     */
    void refresh();

}
