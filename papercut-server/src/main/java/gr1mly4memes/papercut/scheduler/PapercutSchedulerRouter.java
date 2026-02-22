package gr1mly4memes.papercut.scheduler;

import org.bukkit.craftbukkit.scheduler.CraftTask;
import org.bukkit.plugin.Plugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import io.papermc.paper.threadedregions.scheduler.FoliaGlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.FallbackRegionScheduler;

public final class PapercutSchedulerRouter {

    private PapercutSchedulerRouter() {}

    public static CraftTask route(
            CraftTask task,
            long delay,
            FallbackRegionScheduler fallbackScheduler,
            FoliaGlobalRegionScheduler globalScheduler
    ) {

        Plugin plugin = task.getOwner();

        try {
            // Schedule on region thread if possible
            fallbackScheduler.runDelayed(
                    plugin,
                    org.bukkit.Bukkit.getWorlds().get(0),
                    0, 0,
                    (ScheduledTask t) -> task.run(),
                    delay
            );
        } catch (NoSuchMethodError | UnsupportedOperationException e) {
            // Fallback to global region scheduler
            globalScheduler.runDelayed(
                    plugin,
                    (ScheduledTask t) -> task.run(),
                    delay
            );
        }

        // Return the original task for compatibility with CraftScheduler expectations
        return task;
    }
}
