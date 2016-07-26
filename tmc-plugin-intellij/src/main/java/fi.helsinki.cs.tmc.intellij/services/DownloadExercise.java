package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import java.util.List;

public class DownloadExercise {

    public List<Exercise> startDownloadExercise(TmcCore core,
                                                SettingsTmc settings,
                                                CheckForExistingExercises checker,
                                                ProjectOpener opener) throws Exception {

        Course course = core.getCourseDetails(ProgressObserver.NULL_OBSERVER,
                settings.getCourse()).call();

        List<Exercise> exercises = course.getExercises();
        exercises = checker.clean(exercises);
        core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER, exercises).call();
        return exercises;
    }
}
