package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.PasteWindow;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class PasteService {

    private static final Logger logger = LoggerFactory.getLogger(PasteService.class);

    PasteWindow window;
    Exercise exercise;
    TmcCore core;

    public void showSubmitForm(Project project, TmcCore core) {
        logger.info("Opening paste submit form. @PasteService");
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);
        try {
            this.exercise = CourseAndExerciseManager.get(exerciseCourse[exerciseCourse.length - 2],
                    exerciseCourse[exerciseCourse.length - 1]);
            this.core = core;
            this.window = new PasteWindow();
            window.showSubmit(this);

        } catch (Exception exception) {
            handleException(exception);
            logger.warn("Could not show paste submit form.", exception, exception.getStackTrace());
        }
    }

    public void setWindow(PasteWindow window) {
        this.window = window;
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
        logger.info("Uploading to tmc pastebin.");
        try {
            URI uri = core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                    exercise, message).call();
            window.showResult(uri);
        } catch (Exception exception) {
            logger.info("Uploading to pastebin failed.", exception, exception.getStackTrace());
            handleException(exception);
        }
    }

    private void handleException(Exception exception) {
        logger.info("Handling the exception from uploading failure.");
        if (window != null) {
            window.close();
        }
        Messages.showErrorDialog("Are your credentials correct?\n"
                + "Is this a TMC Exercise?\n"
                + "Are you connected to the internet?\n"
                + exception.getMessage() + " "
                + exception.toString(), "Error while uploading to TMC Pastebin");
        CourseAndExerciseManager.updateAll();
        ProjectListManager.refreshAllCourses();
        exception.printStackTrace();
    }
}
