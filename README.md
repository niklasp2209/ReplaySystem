# ReplaySystem for Minecraft (1.21+)

This project implements a highly efficient and scalable replay system for Minecraft, inspired by Hypixel's approach. By leveraging NMS (NetMinecraftServer) and packet handling, the system asynchronously records various player actions and events, such as `LocationChange`, `BlockBreak`, `Sneaking`, and more. These events are stored in a NoSQL database (MongoDB) and can be played back in-game for a seamless replay experience.

### **Inspired by Hypixel**
This system draws inspiration from Hypixel’s replay implementation, including its use of modular *recordables* for event encapsulation and *map hashing* for world consistency. By adopting these proven concepts, the replay system achieves both robustness and extensibility, making it suitable for small servers and large-scale networks alike. Source: https://hypixel.net/threads/dev-blog-10-replay-system-technical-rundown.3234748/

## Features

### **Recordable Events**
The replay system is built around the concept of *recordables*. Each significant player action or game event is encapsulated as a *recordable*, making the system modular and extensible. Current recordable events include:
- **Location Changes**: Tracks player movement in precise detail.
- **Block Interactions**: Records block placements, breaks, and interactions.
- **Player States**: Captures changes like sneaking, sprinting, or interacting with items.
- **Custom Events**: Easily extendable to include new event types as needed.

### **Asynchronous Data Storage**
All recordable events are stored asynchronously in MongoDB. This approach minimizes server performance overhead, ensuring smooth gameplay while recording data in real-time. The asynchronous design also allows for scalability in larger environments with many concurrent players.

### **NMS and Packet Handling**
The system uses low-level NMS integration and direct packet handling to capture player actions and events. By bypassing higher-level APIs, it ensures maximum performance and precision when recording gameplay.

### **Replay Playback**
Players can view their recordings in an intuitive replay viewer. Features include:
- **Start/Pause/Restart**: Control the flow of the replay at any time.
- **Time Navigation**: Jump to specific points in the recording.
- **Multiplayer Viewing**: Enable multiple players to watch the same replay simultaneously (optional).

### **MongoDB Integration**
MongoDB serves as the backbone for storing all recorded data. Its NoSQL nature provides flexibility in managing complex data structures like player actions and map states, while supporting fast retrieval for playback.
