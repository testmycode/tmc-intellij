package fi.helsinki.cs.tmc.intellij.services;


import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gets the list of exercises stored on drive.
 */
public class CheckForExistingExercises {

    public List<Exercise> clean(List<Exercise> exercises) {
        exercises.removeAll(getListOfDownloadedExercises(exercises));
        return exercises;
    }

    public List<Exercise> getListOfDownloadedExercises(List<Exercise> exercises) {
        List<Exercise> existing = new ArrayList<Exercise>();
        for (Exercise exercise : exercises) {
            if (exercise.isDownloaded(TmcSettingsManager.get().getTmcProjectDirectory())) {
                existing.add(exercise);
            }
        }
        return existing;
    }
}