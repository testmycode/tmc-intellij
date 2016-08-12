package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;


/**
 * Offers method to upload exercises.
 */
public class ExerciseUploadingService {

    public void startUploadExercise(Project project, TmcCore core, ObjectFinder finder,
                                    CheckForExistingExercises checker,
                                    SubmissionResultHandler handler,
                                    SettingsTmc settings,
                                    CourseAndExerciseManager courseAndExerciseManager,
                                    ThreadingService threadingService) {

        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);

        if (!courseAndExerciseManager.isCourseInDatabase(getCourseName(exerciseCourse))) {
            Messages.showErrorDialog(project, "Project not identified as TMC exercise", "Error");
            return;
        }

        Exercise exercise = courseAndExerciseManager
                .get(getCourseName(exerciseCourse),
                        getExerciseName(exerciseCourse));
        getResults(project, exercise, core, handler, threadingService);
        courseAndExerciseManager.updateSingleCourse(getCourseName(exerciseCourse),
                checker, finder, settings);
    }

    private void getResults(final Project project,
                            final Exercise exercise, final TmcCore core,
                            final SubmissionResultHandler handler,
                            ThreadingService threadingService) {

        threadingService.runWithNotification(new Runnable() {
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
    }

    private String getCourseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 2];
    }

    private String getExerciseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 1];
    }

}
