package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.intellij.services.persistence.ExerciseDatabase;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentExerciseDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains the ExerciseDatabase. */
public class ExerciseDatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDatabaseManager.class);

    private ExerciseDatabaseManager() {}

    private static PersistentExerciseDatabase persistent() {
        return PersistentExerciseDatabase.getInstance();
    }

    public static synchronized ExerciseDatabase get() {
        logger.info("Get ExerciseDatabase. @ExerciseDatabaseManager.");
        PersistentExerciseDatabase p = persistent();
        if (p.getExerciseDatabase() == null) {
            p.setExerciseDatabase(new ExerciseDatabase());
        }
        return p.getExerciseDatabase();
    }

    public static synchronized void setup() {
        logger.info("Setup ExerciseDatabase. @ExerciseDatabaseManager.");
        PersistentExerciseDatabase p = persistent();
        if (p.getExerciseDatabase() == null) {
            p.setExerciseDatabase(new ExerciseDatabase());
        }
    }
}
