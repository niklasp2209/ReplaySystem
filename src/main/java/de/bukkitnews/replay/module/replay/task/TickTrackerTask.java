package de.bukkitnews.replay.module.replay.task;

import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.replay.ReplayModule;
import lombok.Getter;
import org.bukkit.Bukkit;

public class TickTrackerTask {


    @Getter
    private static long currentTick = 0;

    /**
     * Starts tracking the current tick by incrementing it every game tick.
     * This method schedules a repeating task that increments the {@link #currentTick} every tick.
     */
    public static void startTracking(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ReplayModule.instance.getReplaySystem(), () ->{
            currentTick++;
        }, 0L, 1L);
    }
}
