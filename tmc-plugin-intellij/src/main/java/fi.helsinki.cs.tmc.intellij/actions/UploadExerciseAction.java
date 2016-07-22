package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class UploadExerciseAction extends AnAction{

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        uploadExercise(project, TmcCoreHolder.get(), new ObjectFinder());
    }

    public void uploadExercise(Project project, TmcCore core, ObjectFinder finder){
        String path = project.getBasePath();
        String[] exerciseCourse = new String[2];
        if (path.contains("/")) {
            exerciseCourse = path.split("/");
        } else {
            String backslash = " \\ ";
            backslash.trim();
            exerciseCourse = path.split(backslash);
        }

        Course course = finder.findCourseByName(exerciseCourse[exerciseCourse.length - 2], core);
        Exercise exercise = finder.findExerciseByName(course, exerciseCourse[exerciseCourse.length - 1]);

        getResults(project, exercise, core);

    }

    private void getResults(Project project, Exercise exercise, TmcCore core) {
        try {
            SubmissionResult result = core.submit(ProgressObserver.NULL_OBSERVER, exercise).call();
            Messages.showMessageDialog(project, result.toString(), "Result", Messages.getQuestionIcon());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
