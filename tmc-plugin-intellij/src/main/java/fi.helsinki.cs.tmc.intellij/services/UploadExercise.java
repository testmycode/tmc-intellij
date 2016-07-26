package fi.helsinki.cs.tmc.intellij.services;


import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;

import com.intellij.openapi.project.Project;


public class UploadExercise {

    public void startUploadExercise(Project project, TmcCore core, ObjectFinder finder) {
        PathResolver pathResolver = new PathResolver();
        String[] exerciseCourse = pathResolver.getStrings(project);
        Course course = finder.findCourseByName(exerciseCourse[exerciseCourse.length - 2], core);
        Exercise exercise = finder.findExerciseByName(course,
                exerciseCourse[exerciseCourse.length - 1]);
        getResults(project, exercise, core);
    }

    private void getResults(Project project, Exercise exercise, TmcCore core) {
        try {
            SubmissionResult result = core.submit(ProgressObserver.NULL_OBSERVER, exercise).call();
            SubmissionResultHandler.showResultMessage(exercise, result, project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
