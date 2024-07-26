package sh.fountain.fountain.runtime.scheduling;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.api.scheduling.TaskScheduler;
import sh.fountain.fountain.runtime.plugin.FountainPlugin;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Injectable
public class BukkitScheduler implements TaskScheduler {

    private final FountainPlugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    public BukkitScheduler(final FountainPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = Bukkit.getServer().getScheduler();
    }

    @Override
    public int scheduleSyncDelayedTask(final Runnable task, final long delay) {
        return scheduler.scheduleSyncDelayedTask(plugin, task, delay);
    }

    @Override
    public int scheduleSyncDelayedTask(final Runnable task) {
        return scheduler.scheduleSyncDelayedTask(plugin, task);
    }

    @Override
    public int scheduleSyncRepeatingTask(final Runnable task, final long delay, final long period) {
        return scheduler.scheduleSyncRepeatingTask(plugin, task, delay, period);
    }

    @Override
    public <T> Future<T> callSyncMethod(final Callable<T> task) {
        return scheduler.callSyncMethod(plugin, task);
    }

    @Override
    public void cancelTask(final int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void cancelTasks() {
        scheduler.cancelTasks(plugin);
    }

    @Override
    public boolean isCurrentlyRunning(final int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }

    @Override
    public boolean isQueued(final int taskId) {
        return scheduler.isQueued(taskId);
    }

    @Override
    public List<BukkitWorker> getActiveWorkers() {
        return scheduler.getActiveWorkers();
    }

    @Override
    public List<BukkitTask> getPendingTasks() {
        return scheduler.getPendingTasks();
    }

    @Override
    public BukkitTask runTask(final Runnable task) throws IllegalArgumentException {
        return scheduler.runTask(plugin, task);
    }

    @Override
    public void runTask(final Consumer<BukkitTask> task) throws IllegalArgumentException {
        scheduler.runTask(plugin, task);
    }

    @Override
    public BukkitTask runTaskAsynchronously(final Runnable task) throws IllegalArgumentException {
        return scheduler.runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runTaskAsynchronously(final Consumer<BukkitTask> task) throws IllegalArgumentException {
        scheduler.runTaskAsynchronously(plugin, task);
    }

    @Override
    public BukkitTask runTaskLater(final Runnable task, final long delay) throws IllegalArgumentException {
        return scheduler.runTaskLater(plugin, task, delay);
    }

    @Override
    public void runTaskLater(final Consumer<BukkitTask> task, final long delay) throws IllegalArgumentException {
        scheduler.runTaskLater(plugin, task, delay);
    }

    @Override
    public BukkitTask runTaskLaterAsynchronously(final Runnable task, final long delay) throws IllegalArgumentException {
        return scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(final Consumer<BukkitTask> task, final long delay) throws IllegalArgumentException {
        scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    @Override
    public BukkitTask runTaskTimer(final Runnable task, final long delay, final long period) throws IllegalArgumentException {
        return scheduler.runTaskTimer(plugin, task, delay, period);
    }

    @Override
    public void runTaskTimer(final Consumer<BukkitTask> task, final long delay, final long period) throws IllegalArgumentException {
        scheduler.runTaskTimer(plugin, task, delay, period);
    }

    @Override
    public BukkitTask runTaskTimerAsynchronously(final Runnable task, final long delay, final long period) throws IllegalArgumentException {
        return scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    @Override
    public void runTaskTimerAsynchronously(final Consumer<BukkitTask> task, final long delay, final long period) throws IllegalArgumentException {
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    @Override
    public Executor getMainThreadExecutor() {
        return scheduler.getMainThreadExecutor(plugin);
    }
}
