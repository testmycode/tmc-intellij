package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.PasteWindow;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.net.URI;

public class PasteService {

    PasteWindow window;
    Exercise exercise;
    TmcCore core;

    public void uploadExercise(Project project, TmcCore core) {
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);
        try {
            this.exercise = CourseAndExerciseManager.get(exerciseCourse[exerciseCourse.length - 2],
                    exerciseCourse[exerciseCourse.length - 1]);
            this.core = core;
            this.window = new PasteWindow();
            window.showSubmit(this);

        } catch (Exception exception) {
            Messages.showErrorDialog(project,"Are your credentials correct?\n"
                    + "Is this a TMC Exercise?\n"
                    + "Are you connected to the internet?\n"
                    + exception.getMessage() + " "
                    + exception.toString(), "Error while uploading to Pastebin");
            ProjectListManager.refreshAllCourses();
        }
    }

    public PasteWindow getWindow() {
        return window;
    }

    private static URI sendPaste(String message, Exercise exercise, TmcCore core) {
        try {
            return core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                    exercise, message).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getCourseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 2];
    }

    private static String getExerciseName(String[] courseAndExercise) {
        return courseAndExercise[courseAndExercise.length - 1];
    }

    public String getExerciseName() {
        return exercise.getName();
    }

    public void uploadToTmcPastebin(String message) {
        try {
            URI uri = core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                    exercise, message).call();
            window.showResult(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
