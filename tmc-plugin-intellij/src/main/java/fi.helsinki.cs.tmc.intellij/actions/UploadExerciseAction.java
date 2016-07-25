package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

public class UploadExerciseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        OperationInProgressNotification note = new OperationInProgressNotification("Uploading");
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        uploadExercise(project, TmcCoreHolder.get(), new ObjectFinder());
        note.hide();
    }

    private void uploadExercise(Project project, TmcCore core, ObjectFinder finder) {
        String path = project.getBasePath();
        String[] exerciseCourse = new String[2];
        if (path != null) {
            if (path.contains("/")) {
                exerciseCourse = path.split("/");
            } else {
                String backslash = " \\ ";
                backslash = backslash.trim();
                exerciseCourse = path.split(backslash);
            }
        }

        Course course = finder.findCourseByName(exerciseCourse[exerciseCourse.length - 2], core);
        Exercise exercise = finder.findExerciseByName(course,
                exerciseCourse[exerciseCourse.length - 1]);

        getResults(project, exercise, core);

    }

    private void getResults(Project project, Exercise exercise, TmcCore core) {
        try {
            SubmissionResult result = core.submit(ProgressObserver.NULL_OBSERVER, exercise).call();
            System.out.println(result);
            SubmissionResultHandler.showResultMessage(exercise, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
