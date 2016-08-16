package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.actions.RunTestsAction;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;


/**
 * Offers static methods to upload exercises.
 */
public class ExerciseUploadingService {

    public static void startUploadExercise(Project project, TmcCore core, ObjectFinder finder,
                                           CheckForExistingExercises checker,
                                           SubmissionResultHandler handler,
                                           SettingsTmc settings) {
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);

        if (!CourseAndExerciseManager.isCourseInDatabase(getCourseName(exerciseCourse))) {
            Messages.showErrorDialog(project, "Project not identified as TMC exercise", "Error");
            return;
        }

        Exercise exercise = CourseAndExerciseManager
                .get(getCourseName(exerciseCourse),
                        getExerciseName(exerciseCourse));
        getResults(project, exercise, core, handler);
        CourseAndExerciseManager.updateSingleCourse(getCourseName(exerciseCourse),
                checker, finder, settings);

    }

    private static void getResults(final Project project,
                                   final Exercise exercise, final TmcCore core,
                                   final SubmissionResultHandler handler) {
        ThreadingService.runWithNotification(new Runnable() {
            @Override
            public void run() {
                try {
                    final SubmissionResult result = core
                            .submit(ProgressObserver.NULL_OBSERVER, exercise).call();
                    handler.showResultMessage(exercise, result, project);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }, "Uploading exercise, this may take several minutes", project);
        RunTestsAction.displayTestWindow();
    }

    private static String getCourseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 2];
    }

    private static String getExerciseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 1];
    }

}
