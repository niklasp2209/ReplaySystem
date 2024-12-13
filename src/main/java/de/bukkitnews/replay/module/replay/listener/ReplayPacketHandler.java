package de.bukkitnews.replay.module.replay.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation;
import de.bukkitnews.replay.module.replay.ReplayModule;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.SetEquipmentRecordable;
import de.bukkitnews.replay.module.replay.data.recordable.recordables.SwingHandRecordable;
import de.bukkitnews.replay.module.replay.data.recording.ActiveRecording;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ReplayPacketHandler implements PacketListener {

    /**
     * Handles the reception of player packets to record relevant actions.
     *
     * @param event The PacketReceiveEvent triggered by the player action.
     */
    @Override
    public void onPacketReceive(@NonNull PacketReceiveEvent event) {
        Player player = (Player) event.getPlayer();

        Optional<ActiveRecording> activeRecordingOpt = ReplayModule.instance.getRecordingHandler().getPlayerActiveRecording(player);

        if (activeRecordingOpt.isEmpty()) {
            return;
        }

        ActiveRecording activeRecording = activeRecordingOpt.get();

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            WrapperPlayClientAnimation playClientAnimation = new WrapperPlayClientAnimation(event);
            SwingHandRecordable swingHandRecordable = new SwingHandRecordable(player.getUniqueId(), playClientAnimation.getHand().getId());
            ReplayModule.instance.getRecordingHandler().addRecordable(activeRecording, swingHandRecordable);
        }
        else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            if (player.getEquipment() == null) {
                return;
            }

            ItemStack mainHand = player.getEquipment().getItemInMainHand();
            ItemStack offHand = player.getEquipment().getItemInOffHand();
            ItemStack helmet = player.getEquipment().getHelmet();
            ItemStack chestplate = player.getEquipment().getChestplate();
            ItemStack leggings = player.getEquipment().getLeggings();
            ItemStack boots = player.getEquipment().getBoots();

            SetEquipmentRecordable setEquipmentRecordable = new SetEquipmentRecordable(
                    player.getUniqueId(), mainHand, offHand, helmet, chestplate, leggings, boots);
            ReplayModule.instance.getRecordingHandler().addRecordable(activeRecording, setEquipmentRecordable);
        }
    }

}