package fi.helsinki.cs.tmc.intellij.holders;


import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.intellij.services.ExerciseDatabase;
import fi.helsinki.cs.tmc.intellij.services.PersistentExerciseDatabase;

import com.intellij.openapi.components.ServiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the ExerciseDatabase.
 */
public class ExerciseDatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDatabaseManager.class);
    private static final PersistentExerciseDatabase persistentExerciseDatabase =
            ServiceManager.getService(PersistentExerciseDatabase.class);

    private ExerciseDatabaseManager() {

    }

    public static synchronized ExerciseDatabase get() {
        logger.info("Get ExerciseDatabase from ExerciseDatabaseManager.");
        if (persistentExerciseDatabase.getExerciseDatabase() == null) {
            persistentExerciseDatabase.setExerciseDatabase(new ExerciseDatabase());
        }
        return persistentExerciseDatabase.getExerciseDatabase();
    }

    public static synchronized void setup() {
        logger.info("Setup ExerciseDatabase at ExerciseDatabaseManager.");
        if (persistentExerciseDatabase.getExerciseDatabase() == null) {
            persistentExerciseDatabase.setExerciseDatabase(new ExerciseDatabase());
        }

    }

    public static synchronized void set(ExerciseDatabase settings) {
        logger.info("Set ExerciseDatabase at ExerciseDatabaseManager.");
        persistentExerciseDatabase.setExerciseDatabase(settings);
    }

}
