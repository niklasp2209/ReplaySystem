package de.bukkitnews.replay.module.replay.database;

import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import de.bukkitnews.replay.module.replay.database.objects.CameraObject;
import de.bukkitnews.replay.module.replay.database.objects.RecordableObject;
import de.bukkitnews.replay.module.replay.database.objects.RecordingObject;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DatabaseObjects {

    private final CameraObject cameraObject;
    private final RecordingObject recordingObject;
    private final RecordableObject recordableObject;

    public DatabaseObjects(@NonNull MongoConnectionManager mongoConnectionManager) {
        this.cameraObject = new CameraObject(mongoConnectionManager);
        this.recordingObject = new RecordingObject(mongoConnectionManager);
        this.recordableObject = new RecordableObject(mongoConnectionManager);
    }
}