package sh.fountain.fountain.api.scheduling;

import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import sh.fountain.fountain.api.dependency_injection.Injectable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * An object to schedule tasks for this plugin.
 * <p>
 * Compared to {@link org.bukkit.scheduler.BukkitScheduler}, this class already has the plugin reference injected
 * for all applicable functions.
 * </p>
 * For documentation on the methods, see {@link org.bukkit.scheduler.BukkitScheduler}.
 */
@Injectable
public interface TaskScheduler {
    int scheduleSyncDelayedTask(Runnable task, long delay);

    int scheduleSyncDelayedTask(Runnable task);

    int scheduleSyncRepeatingTask(Runnable task, long delay, long period);

    <T> Future<T> callSyncMethod(Callable<T> task);

    void cancelTask(int taskId);

    void cancelTasks();

    boolean isCurrentlyRunning(int taskId);

    boolean isQueued(int taskId);

    List<BukkitWorker> getActiveWorkers();

    List<BukkitTask> getPendingTasks();

    BukkitTask runTask(Runnable task) throws IllegalArgumentException;

    void runTask(Consumer<BukkitTask> task) throws IllegalArgumentException;

    BukkitTask runTaskAsynchronously(Runnable task) throws IllegalArgumentException;

    void runTaskAsynchronously(Consumer<BukkitTask> task) throws IllegalArgumentException;

    BukkitTask runTaskLater(Runnable task, long delay) throws IllegalArgumentException;

    void runTaskLater(Consumer<BukkitTask> task, long delay) throws IllegalArgumentException;

    BukkitTask runTaskLaterAsynchronously(Runnable task, long delay) throws IllegalArgumentException;

    void runTaskLaterAsynchronously(Consumer<BukkitTask> task, long delay) throws IllegalArgumentException;

    BukkitTask runTaskTimer(Runnable task, long delay, long period) throws IllegalArgumentException;

    void runTaskTimer(Consumer<BukkitTask> task, long delay, long period) throws IllegalArgumentException;

    BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) throws IllegalArgumentException;

    void runTaskTimerAsynchronously(Consumer<BukkitTask> task, long delay, long period) throws IllegalArgumentException;

    Executor getMainThreadExecutor();
}
