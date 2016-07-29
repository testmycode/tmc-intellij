package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import java.util.List;

public class ExerciseDownloadingService {

    public static List<Exercise> startDownloadExercise(TmcCore core,
                                                       SettingsTmc settings,
                                                       CheckForExistingExercises checker,
                                                       ProjectOpener opener) throws Exception {

        ObjectFinder finder = new ObjectFinder();
        Course course = finder.findCourseByName(settings.getCourse().getName(), core);

        List<Exercise> exercises = course.getExercises();
        exercises = checker.clean(exercises, settings);

        core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER, exercises).call();
        CourseAndExerciseManager.updateSingleCourse(course.getName(), checker, finder, settings);
        return exercises;
    }
}
