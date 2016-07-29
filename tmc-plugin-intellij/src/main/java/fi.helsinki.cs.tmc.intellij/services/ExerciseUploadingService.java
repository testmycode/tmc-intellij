package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;


/**
 * Offers static methods to upload exercises.
 */
public class ExerciseUploadingService {

    public static void startUploadExercise(Project project, TmcCore core, ObjectFinder finder,
                                           CheckForExistingExercises checker,
                                           SubmissionResultHandler handler) {
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);

        try {
            Course course = finder.findCourseByName(getCourseName(exerciseCourse), core);
            Exercise exercise = finder.findExerciseByName(course,
                    getExerciseName(exerciseCourse));

            getResults(project, exercise, core, handler);
            CourseAndExerciseManager.updateSinglecourse(course.getName(),
                    checker, finder);
        } catch (Exception exception) {
            Messages.showErrorDialog(project, "Are your credentials correct?\n"
                    + "Is this a TMC Exercise?\n"
                    + "Are you connected to the internet?\n"
                    + exception.getMessage() + " "
                    + exception.toString(), "Error while submitting");
        }

    }

    private static void getResults(Project project, Exercise exercise, TmcCore core,
                                   SubmissionResultHandler handler) {
        try {
            SubmissionResult result = core.submit(ProgressObserver.NULL_OBSERVER, exercise).call();
            handler.showResultMessage(exercise, result, project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCourseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 2];
    }

    private static String getExerciseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 1];
    }

}
