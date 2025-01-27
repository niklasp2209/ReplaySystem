package de.bukkitnews.replay.module.replay.listener.bukkit;

import de.bukkitnews.replay.module.replay.data.replay.Replay;
import de.bukkitnews.replay.module.replay.handler.ReplayHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@RequiredArgsConstructor
public class ReplayListener implements Listener {

    private final @NotNull ReplayHandler replayHandler;

    /**
     * Handles right-click actions on air or block to control the replay.
     *
     * @param event The PlayerInteractEvent triggered by the player.
     */
    @EventHandler
    public void handleRightClick(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Player player = event.getPlayer();
        Optional<Replay> replayOpt = replayHandler.getReplayForPlayer(player);
        if (replayOpt.isEmpty()) {
            return;
        }

        Replay replay = replayOpt.get();

        event.setCancelled(true);

        switch (item.getType()) {
            case REPEATER:
                replayHandler.restartReplay(replay);
                break;
            case GREEN_BANNER:
                replay.playReplay();
                break;
            case YELLOW_BANNER:
                replay.pauseReplay();
                break;
            case LIME_BANNER:
                replayHandler.stopReplay(replay);
                break;
        }
    }
}