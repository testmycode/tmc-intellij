package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.services.Errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.Exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.PasteWindow;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class PasteService {

    private PasteWindow window;
    private Exercise exercise;
    private TmcCore core;
    private static final Logger logger = LoggerFactory.getLogger(PasteService.class);

    public void showSubmitForm(Project project, TmcCore core) {
        logger.info("Opening paste submit form. @PasteService");
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);

        this.exercise = new CourseAndExerciseManager().getExercise(getCourseName(exerciseCourse),
                getExerciseNameFromArray(exerciseCourse));
        this.core = core;
        this.window = new PasteWindow();
        window.showSubmit(this);
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

    public void uploadToTmcPastebin(String message,
                                    CourseAndExerciseManager courseAndExerciseManager,
                                    ProjectListManager projectListManager) {

        logger.info("Uploading to tmc pastebin. @PasteService");
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    URI uri = core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                            exercise, message).call();
                    window.showResult(uri);
                    updateProjectView(courseAndExerciseManager, projectListManager);
                } catch (TmcCoreException exception) {
                    logger.info("Uploading to pastebin failed. @PasteService",
                            exception, exception.getStackTrace());
                    handleException(exception);
                } catch (Exception exception) {
                    logger.info("Uploading to pastebin failed. @PasteService",
                            exception, exception.getStackTrace());
                    new ErrorMessageService().showMessage(exception,
                            "Error while uploading to TMC Pastebin.", true);
                }
            }
        });
    }

    private void handleException(TmcCoreException exception) {

        logger.info("Handling the exception from uploading failure. @PasteService");
        closeWindowIfExists();

        new ErrorMessageService().showMessage(exception, false);
        exception.printStackTrace();
    }

    private void closeWindowIfExists() {
        if (window != null) {
            logger.info("Closing window. @PasteService");
            window.close();
        }
    }

    private void updateProjectView(CourseAndExerciseManager courseAndExerciseManager,
                                   ProjectListManager projectListManager) {
        logger.info("Updating project view. @PasteService");

        courseAndExerciseManager.initiateDatabase();
        projectListManager.refreshAllCourses();
    }

    private String getCourseName(String[] exerciseCourse) {
        return exerciseCourse[exerciseCourse.length - 2];
    }

    private String getExerciseNameFromArray(String[] exerciseCourse) {
        return exerciseCourse[exerciseCourse.length - 1];
    }

}
