package fi.helsinki.cs.tmc.intellij.services;


import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import java.util.ArrayList;
import java.util.List;

/**
 * Gets the list of exercises stored on drive.
 */
public class CheckForExistingExercises {

    public List<Exercise> clean(List<Exercise> exercises, SettingsTmc settings) {
        exercises.removeAll(getListOfDownloadedExercises(exercises, settings));
        return exercises;
    }

    public List<Exercise> getListOfDownloadedExercises(List<Exercise> exercises, SettingsTmc settingsTmc) {

        List<Exercise> existing = new ArrayList<Exercise>();
        for (Exercise exercise : exercises) {
            if (exercise.isDownloaded(settingsTmc.getTmcProjectDirectory())) {
                existing.add(exercise);
            }
        }
        return existing;
    }
}