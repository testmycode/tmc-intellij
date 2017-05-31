package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.intellij.services.persistence.ExerciseDatabase;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentExerciseDatabase;

import com.intellij.openapi.components.ServiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains the ExerciseDatabase. */
public class ExerciseDatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDatabaseManager.class);
    private static final PersistentExerciseDatabase persistentExerciseDatabase =
            ServiceManager.getService(PersistentExerciseDatabase.class);

    private ExerciseDatabaseManager() {}

    public static synchronized ExerciseDatabase get() {
        logger.info("Get ExerciseDatabase. @ExerciseDatabaseManager.");
        if (persistentExerciseDatabase.getExerciseDatabase() == null) {
            persistentExerciseDatabase.setExerciseDatabase(new ExerciseDatabase());
        }
        return persistentExerciseDatabase.getExerciseDatabase();
    }

    public static synchronized void setup() {
        logger.info("Setup ExerciseDatabase. @ExerciseDatabaseManager.");
        if (persistentExerciseDatabase.getExerciseDatabase() == null) {
            persistentExerciseDatabase.setExerciseDatabase(new ExerciseDatabase());
        }
    }
}
