package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.mojang.authlib.GameProfile;
import de.bukkitnews.replay.exception.EntityCreationException;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.*;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SpawnEntity")
public class SpawnEntityRecordable extends Recordable {

    private @NotNull EntityType entityType;
    private @NotNull Location location;
    private @NotNull UUID bukkitEntityId;
    private @Nullable String playerName;

    public SpawnEntityRecordable(@NotNull Entity entity) {
        this.entityType = entity.getType();
        this.location = entity.getLocation();
        this.bukkitEntityId = entity.getUniqueId();
        if (entityType == EntityType.PLAYER) {
            this.playerName = entity.getName();
        }
    }

    /**
     * Replays the entity spawn event by sending the appropriate spawn packet.
     *
     * @param replay the replay instance
     * @param user   the user receiving the packet
     */
    @Override
    public void replay(@NotNull Replay replay, @NotNull User user) {
        if (entityType == EntityType.PLAYER) {
            replayPlayerSpawn(replay);
        } else {
            try {
                replayEntitySpawn(replay, user);
            } catch (EntityCreationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void replayPlayerSpawn(@NotNull Replay replay) {
        ServerPlayer serverPlayer = ((CraftPlayer) replay.getPlayer()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), playerName);

        ServerPlayer npc = new ServerPlayer(
                serverPlayer.getServer(), serverPlayer.serverLevel().getLevel(), gameProfile, ClientInformation.createDefault());
        npc.setPos(location.getX(), location.getY(), location.getZ());

        ServerGamePacketListenerImpl serverGamePacketListener = serverPlayer.connection;
        SynchedEntityData synchedEntityData = npc.getEntityData();
        synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
        setValue(npc, "c", serverPlayer.connection);

        ServerEntity npcServerEntity = new ServerEntity(serverPlayer.serverLevel(), serverPlayer, 0, false, packet -> {
        }, Set.of());
        serverGamePacketListener.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
        serverGamePacketListener.send(new ClientboundAddEntityPacket(npc, npcServerEntity));

        replay.getSpawnedEntities().put(bukkitEntityId, npc.getId());
    }

    private void replayEntitySpawn(@NotNull Replay replay, @NotNull User user) throws EntityCreationException {
        int entityId = generateEntityId();
        com.github.retrooper.packetevents.protocol.entity.type.EntityType entityType1 = EntityTypes.getByName(entityType.getKey().toString());

        user.sendPacket(new WrapperPlayServerSpawnEntity(
                entityId, UUID.randomUUID(),
                entityType1, SpigotConversionUtil.fromBukkitLocation(location),
                0, 0, null));
        replay.getSpawnedEntities().put(bukkitEntityId, entityId);
    }

    private int generateEntityId() throws EntityCreationException {
        try {
            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Field field = entityClass.getDeclaredField("c");
            field.setAccessible(true);
            AtomicInteger entityCounter = (AtomicInteger) field.get(null);
            return entityCounter.incrementAndGet();
        } catch (ClassNotFoundException | NoSuchFieldException |
                 IllegalAccessException e) {
            throw new EntityCreationException("Failed to generate entity ID", e);
        }
    }

    private void setValue(@NotNull Object packet, @NotNull String fieldName, @NotNull Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set value for field " + fieldName, e);
        }
    }
}
