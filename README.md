# ReplaySystem for Minecraft (1.21+)

This project implements a replay system for Minecraft, utilizing packets and NMS (NetMinecraftServer) to asynchronously store various player actions and events such as `LocationChange`, `BlockBreak`, `Sneaking`, and more in a NoSQL database (MongoDB). Players can retrieve and play back their past gameplay recordings.

## Features

- **Asynchronous Storage**: All relevant player events are stored asynchronously in a MongoDB database.
- **Event Types**: Tracks multiple events such as location changes (LocationChange), block breaks (BlockBreak), sneaking, and more.
- **NMS and Packet Handling**: Uses NMS and PacketEvents to capture player actions at the lowest level.
- **Replay Playback**: Players can watch their recordings through the replay viewer with options to start, pause, and restart the replay.
- **MongoDB Integration**: Stores all data in a MongoDB database for easy, scalable management of recordings.

## Requirements

- **Minecraft Server Version**: 1.21+
- **Dependencies**:
  - [PacketEvents](https://github.com/Retrooper/PacketEvents)
  - [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/)
  - [NMS (NetMinecraftServer)] (utilized for low-level packet handling)
