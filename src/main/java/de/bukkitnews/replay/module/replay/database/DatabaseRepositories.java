package de.bukkitnews.replay.module.replay.database;

import com.mongodb.client.MongoCollection;
import de.bukkitnews.replay.module.database.mongodb.MongoConnectionManager;
import de.bukkitnews.replay.module.replay.data.recordable.Recordable;
import de.bukkitnews.replay.module.replay.data.recording.Recording;
import de.bukkitnews.replay.module.replay.data.recording.RecordingArea;
import de.bukkitnews.replay.module.replay.database.objects.CameraRepository;
import de.bukkitnews.replay.module.replay.database.objects.RecordableRepository;
import de.bukkitnews.replay.module.replay.database.objects.RecordingRepository;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DatabaseRepositories {

    private final CameraRepository cameraRepository;
    private final RecordingRepository recordingRepository;
    private final RecordableRepository recordableRepository;

    /**
     * Initializes the database objects for accessing MongoDB collections.
     * <p>
     * This class acts as a container for managing access to the three primary database collections:
     * - CameraObject: Manages operations for the "cameras" collection.
     * - RecordingObject: Manages operations for the "recordings" collection.
     * - RecordableObject: Manages operations for the "recordables" collection.
     * </p>
     *
     * @param mongoConnectionManager The connection manager used to access the MongoDB database.
     */
    public DatabaseRepositories(@NonNull MongoConnectionManager mongoConnectionManager) {
        MongoCollection<RecordingArea> cameraCollection = mongoConnectionManager.getDatabase()
                .getCollection("cameras", RecordingArea.class);
        MongoCollection<Recording> recordingCollection = mongoConnectionManager.getDatabase()
                .getCollection("recordings", Recording.class);
        MongoCollection<Recordable> recordableCollection = mongoConnectionManager.getDatabase()
                .getCollection("recordables", Recordable.class);

        this.cameraRepository = new CameraRepository(cameraCollection);
        this.recordingRepository = new RecordingRepository(recordingCollection);
        this.recordableRepository = new RecordableRepository(recordableCollection);
    }
}