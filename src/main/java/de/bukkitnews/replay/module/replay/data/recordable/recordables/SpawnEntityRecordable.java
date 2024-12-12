package de.bukkitnews.replay.module.replay.data.recordable.recordables;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.mojang.authlib.GameProfile;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.replay.Replay;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

    private EntityType entityType;
    private Location location;
    private UUID bukkitEntityId;
    private String playerName;

    public SpawnEntityRecordable(Entity entity) {
        this.entityType = entity.getType();
        this.location = entity.getLocation();
        this.bukkitEntityId = entity.getUniqueId();
        if(this.entityType == EntityType.PLAYER){
            this.playerName = entity.getName();
        }
    }

    /**
     * Replays the entity spawn event by sending the appropriate spawn packet.
     *
     * @param replay the replay instance
     * @param user the user receiving the packet
     * @throws Exception if there is an issue during the replay
     */
    @Override
    public void replay(Replay replay, User user) throws Exception {
        if(entityType == EntityType.PLAYER){

            Player player = replay.getViewer();
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();
            MinecraftServer minecraftServer = serverPlayer.getServer();
            ServerLevel serverLevel = serverPlayer.serverLevel().getLevel();
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), playerName);

            ServerPlayer npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile, ClientInformation.createDefault());
            npc.setPos(location.getX(), location.getY(), location.getZ());

            ServerGamePacketListenerImpl serverGamePacketListener = serverPlayer.connection;

            SynchedEntityData synchedEntityData = npc.getEntityData();
            synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
            setValue(npc, "c", ((CraftPlayer) player).getHandle().connection);


            ServerEntity npcServerEntity = new ServerEntity(serverPlayer.serverLevel(), serverPlayer, 0, false, packet -> {
            }, Set.of());
            serverGamePacketListener.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
            serverGamePacketListener.send(new ClientboundAddEntityPacket(npc, npcServerEntity));

            replay.getSpawnedEntities().put(bukkitEntityId, npc.getId());

        } else {
            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Field field = entityClass.getDeclaredField("c");
            field.setAccessible(true);
            AtomicInteger ENTITY_COUNTER = (AtomicInteger) field.get(null);
            int entityId = ENTITY_COUNTER.incrementAndGet();

            com.github.retrooper.packetevents.protocol.entity.type.EntityType entityType1 = EntityTypes.getByName(entityType.getKey().toString());

            WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                    entityId, UUID.randomUUID(),
                    entityType1, SpigotConversionUtil.fromBukkitLocation(location),
                    0, 0, null);

            user.sendPacket(spawnEntityPacket);
            replay.getSpawnedEntities().put(bukkitEntityId, entityId);
        }
    }

    private void setValue(Object packet, String fieldName, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
