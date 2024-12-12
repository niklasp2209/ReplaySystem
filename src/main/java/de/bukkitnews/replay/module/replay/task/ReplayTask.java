package de.bukkitnews.replay.module.replay.task;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import de.bukkitnews.replay.framework.util.MessageUtil;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import de.bukkitnews.replay.module.replay.handle.ReplayHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class ReplayTask extends BukkitRunnable {

    private final Replay replay;
    private final ReplayHandler replayHandler;
    private long currentTick = 0;
    private User user;
    private boolean paused = true;

    public ReplayTask(Replay replay) {
        this.replay = replay;
        this.replayHandler = ReplayModule.instance.getReplayHandler();
        this.user = PacketEvents.getAPI().getPlayerManager().getUser(replay.getViewer());
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
            replay.getViewer().sendMessage(MessageUtil.getMessage("replay_ended"));
            replayHandler.stopReplay(replay);
            return;
        }

        if (replay.getRecordableQueue().size() < 100) {
            long nextTick = currentTick + replay.getRecordableQueue().size();
            replayHandler.loadRecordables(replay, nextTick);
        }

        if (replay.getRecordableQueue().isEmpty()) {
            System.out.println("Replay is buffering");
            return;
        }

        replay.getViewer().setExp((float) currentTick / replay.getRecording().getTickDuration());

        Optional.ofNullable(replay.getRecordableQueue().poll())
                .ifPresentOrElse(
                        tickRecordables -> {
                            try {
                                for (Recordable recordable : tickRecordables) {
                                    System.out.println("Replaying: " + recordable.getClass().getName());
                                    recordable.replay(replay, user);
                                }
                            } catch (Exception exception) {
                                System.out.println("Tick: " + currentTick);
                                exception.printStackTrace();
                            }
                        },
                        () -> System.out.println("No recordables for tick " + currentTick)
                );

        currentTick++;
    }

    /**
     * Restarts the replay from the beginning. The replay is paused, and the tick counter is reset.
     */
    public void restart() {
        this.paused = true;
        currentTick = 0L;
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

