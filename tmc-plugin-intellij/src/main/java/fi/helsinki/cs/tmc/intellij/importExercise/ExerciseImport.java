package fi.helsinki.cs.tmc.intellij.importExercise;

import java.io.IOException;

public class ExerciseImport {
    /**
     * Handles Exercise import also possibly decides what to import in future.
     * @param path
     * @throws IOException
     */
    public static void importExercise(String path) throws IOException {
        NewProjectUtilModified.importExercise(path);
    }
}

