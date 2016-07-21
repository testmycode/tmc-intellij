package fi.helsinki.cs.tmc.intellij.actions;


import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import java.util.ArrayList;
import java.util.List;

public class CheckForExistingExercises {

    public static List<Exercise> clean(List<Exercise> exercises) {
        List<Exercise> existing = new ArrayList<Exercise>();
        for (Exercise exercise : exercises) {
            if (exercise.isDownloaded(TmcSettingsManager.get().getTmcProjectDirectory())) {
                existing.add(exercise);
            }
        }
        exercises.removeAll(existing);
        return exercises;
    }
}
