package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
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
    Project project;

    public void showSubmitForm(Project project, TmcCore core) {
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);
        try {
            this.project = project;
            this.exercise = CourseAndExerciseManager.get(exerciseCourse[exerciseCourse.length - 2],
                    exerciseCourse[exerciseCourse.length - 1]);
            this.core = core;
            this.window = new PasteWindow();
            window.showSubmit(this);

        } catch (Exception exception) {
            handleException(exception);

        }
    }

    public void setCore(TmcCore core) {
        this.core = core;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public PasteWindow getWindow() {
        return window;
    }

    public String getExerciseName() {
        return exercise.getName();
    }

    public void uploadToTmcPastebin(String message) {
        try {
            URI uri = core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                    exercise, message).call();
            window.showResult(uri);
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private void handleException(Exception exception) {
        window.close();
        Messages.showErrorDialog(project,"Are your credentials correct?\n"
                + "Is this a TMC Exercise?\n"
                + "Are you connected to the internet?\n"
                + exception.getMessage() + " "
                + exception.toString(), "Error while uploading to TMC Pastebin");
        CourseAndExerciseManager.updateAll();
        ProjectListManager.refreshAllCourses();
        exception.printStackTrace();
    }
}
