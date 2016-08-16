package fi.helsinki.cs.tmc.intellij.holders;


import fi.helsinki.cs.tmc.intellij.services.ExerciseDatabase;
import fi.helsinki.cs.tmc.intellij.services.PersistentExerciseDatabase;

import com.intellij.openapi.components.ServiceManager;

/**
 * Contains the ExerciseDatabase.
 */
public class ExerciseDatabaseManager {

    private static final PersistentExerciseDatabase persistentExerciseDatabase =
            ServiceManager.getService(PersistentExerciseDatabase.class);

    private ExerciseDatabaseManager() {

    }

    public static synchronized ExerciseDatabase get() {
        if (persistentExerciseDatabase.getExerciseDatabase() == null) {
            persistentExerciseDatabase.setExerciseDatabase(new ExerciseDatabase());
        }
        return persistentExerciseDatabase.getExerciseDatabase();
    }

    public static synchronized void setup() {
        if (persistentExerciseDatabase.getExerciseDatabase() == null) {
            persistentExerciseDatabase.setExerciseDatabase(new ExerciseDatabase());
        }

    }

    public static synchronized void set(ExerciseDatabase settings) {
        persistentExerciseDatabase.setExerciseDatabase(settings);
    }

}
