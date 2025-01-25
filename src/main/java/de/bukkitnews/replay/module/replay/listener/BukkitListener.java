package de.bukkitnews.replay.module.replay.listener;

import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.*;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import de.bukkitnews.replay.module.replay.handle.RecordingHandler;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.Optional;

public class BukkitListener implements Listener {

    @NonNull private final RecordingHandler recordingHandler;

    public BukkitListener() {
        this.recordingHandler = ReplayModule.instance.getRecordingHandler();
    }

    /**
     * Helper method to add a recordable object to an active recording if it exists.
     *
     * @param activeRecording The active recording to add the recordable to.
     * @param recordable The recordable object to be added to the active recording.
     */
    private void recordIfActive(ActiveRecording activeRecording, Recordable recordable) {
        if (activeRecording != null) {
            recordingHandler.addRecordable(activeRecording, recordable);
        } else {
            // Optionally log if no active recording exists for the player
            System.out.println("No active recording found for the entity/player.");
        }
    }

    /**
     * Handles player sneak actions and records them.
     *
     * @param event The PlayerToggleSneakEvent triggered when a player toggles sneaking.
     */
    @EventHandler
    public void handlePlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(player);

        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            /*
            SneakRecordable recordable = new SneakRecordable(player.getUniqueId(), event.isSneaking());
            recordIfActive(activeRecording, recordable);

             */
        }
    }

    /**
     * Handles player sprint actions and records them.
     *
     * @param event The PlayerToggleSprintEvent triggered when a player toggles sprinting.
     */
    @EventHandler
    public void handleOnPlayerSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(player);

        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            SprintRecordable recordable = new SprintRecordable(player.getUniqueId(), event.isSprinting());
            recordIfActive(activeRecording, recordable);
        }
    }

    /**
     * Handles entity damage events and records them.
     *
     * @param event The EntityDamageEvent triggered when an entity is damaged.
     */
    @EventHandler
    public void handleDamage(EntityDamageEvent event) {
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(event.getEntity());

            /*
        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            EntityHurtRecordable entityHurtRecordable = new EntityHurtRecordable(event.getEntity().getUniqueId());
            recordIfActive(activeRecording, entityHurtRecordable);
        }

             */
    }

    /**
     * Handles item drop events and records them.
     *
     * @param event The PlayerDropItemEvent triggered when a player drops an item.
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(event.getPlayer());

            /*
        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            activeRecording.addEntityIfNotAlreadyTracked(event.getItemDrop().getUniqueId());
            ItemDropRecordable itemDropRecordable = new ItemDropRecordable(event.getItemDrop().getUniqueId(),
                    event.getItemDrop().getLocation(),
                    event.getItemDrop().getItemStack());
            recordIfActive(activeRecording, itemDropRecordable);
        }

             */
    }

    /**
     * Handles item pickup events and records them.
     *
     * @param event The EntityPickupItemEvent triggered when an entity picks up an item.
     */
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(event.getEntity());

            /*
        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            ItemPickupRecordable itemPickupRecordable = new ItemPickupRecordable(event.getEntity().getUniqueId(),
                    event.getItem().getUniqueId());
            recordIfActive(activeRecording, itemPickupRecordable);
        }

             */
    }

    /**
     * Handles block place events and records them.
     *
     * @param event The BlockPlaceEvent triggered when a block is placed.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(event.getPlayer());

        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            BlockPlaceRecordable blockPlaceRecordable = new BlockPlaceRecordable(event.getBlockPlaced().getType(),
                    event.getBlock().getLocation());
            recordIfActive(activeRecording, blockPlaceRecordable);
        }
    }

    /**
     * Handles block break events and records them.
     *
     * @param event The BlockBreakEvent triggered when a block is broken.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Optional<ActiveRecording> activeRecordingOpt = recordingHandler.getEntities(event.getPlayer());

        if (activeRecordingOpt.isPresent()) {
            ActiveRecording activeRecording = activeRecordingOpt.get();
            BlockPlaceRecordable blockPlaceRecordable = new BlockPlaceRecordable(event.getBlock().getType(),
                    event.getBlock().getLocation());
            recordIfActive(activeRecording, blockPlaceRecordable);
        }
    }
}