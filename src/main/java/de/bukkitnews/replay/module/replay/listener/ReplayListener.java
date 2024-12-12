package de.bukkitnews.replay.module.replay.listener;

import de.bukkitnews.replay.module.replay.data.replay.Replay;
import de.bukkitnews.replay.module.replay.handle.ReplayHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ReplayListener implements Listener {

    private final ReplayHandler replayHandler;

    public ReplayListener(ReplayHandler replayHandler) {
        this.replayHandler = replayHandler;
    }

    /**
     * Handles right-click actions on air or block to control the replay.
     *
     * @param event The PlayerInteractEvent triggered by the player.
     */
    @EventHandler
    public void handleRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Replay replay = replayHandler.getReplayForPlayer(player);

        if (replay == null) {
            return;
        }

        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

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
            default:
                return;
        }

        event.setCancelled(true);
    }
}