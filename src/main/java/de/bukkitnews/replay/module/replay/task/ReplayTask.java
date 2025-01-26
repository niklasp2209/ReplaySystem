package de.bukkitnews.replay.module.replay.task;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import de.bukkitnews.replay.module.replay.util.MessageUtil;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import de.bukkitnews.replay.module.replay.handler.ReplayHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReplayTask extends BukkitRunnable {

    private final @NotNull Replay replay;
    private final @NotNull ReplayHandler replayHandler;
    private long currentTick = 0;
    private final @NotNull User user;
    private boolean paused = true;

    public ReplayTask(@NotNull ReplayModule replayModule, @NotNull Replay replay) {
        this.replay = replay;
        this.replayHandler = replayModule.getReplayHandler();
        this.user = PacketEvents.getAPI().getPlayerManager().getUser(replay.getPlayer());
    }

    /**
     * Executes the replay task. This method is run every tick by the Bukkit scheduler.
     */
    @Override
    public void run() {
        if (paused) {
            return;
        }

        if (currentTick >= replay.getRecording().getTickDuration()) {
            replay.getPlayer().sendMessage(MessageUtil.getMessage("replay_ended"));
            replayHandler.stopReplay(replay);
            return;
        }

        if (replay.getRecordableQueue().size() < 100) {
            long nextTick = currentTick + replay.getRecordableQueue().size();
            replayHandler.loadRecordables(replay, nextTick);
        }

        replay.getPlayer().setExp((float) currentTick / replay.getRecording().getTickDuration());
        Optional.ofNullable(replay.getRecordableQueue().poll()).ifPresentOrElse(tickRecordables ->
                tickRecordables.forEach(recordable -> recordable.replay(replay, user)),
                () -> System.out.println("No recordables for tick " + currentTick)
        );

        currentTick++;
    }

    /**
     * Restarts the replay from the beginning. The replay is paused, and the tick counter is reset.
     */
    public void restart() {
        this.paused = true;
        this.currentTick = 0L;
    }

    /**
     * Starts the replay. This method resumes the playback from the current tick.
     */
    public void play() {
        this.paused = false;
    }

    /**
     * Pauses the replay. This method stops the playback and keeps the current tick.
     */
    public void pause() {
        this.paused = true;
    }
}

