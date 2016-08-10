package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.PasteWindow;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.net.URI;

public class PasteService {

    private PasteWindow window;
    private Exercise exercise;
    private TmcCore core;

    public void showSubmitForm(Project project, TmcCore core) {
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);

        this.exercise = new CourseAndExerciseManager().get(getCourseName(exerciseCourse),
                getExerciseName(exerciseCourse));
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
        try {
            URI uri = core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                    exercise, message).call();
            window.showResult(uri);
            updateProjectView(courseAndExerciseManager, projectListManager);
        } catch (TmcCoreException exception) {
            handleException(exception);
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Unknown error", "Error while uploading to TMC Pastebin");
        }
    }

    private void handleException(TmcCoreException exception) {
        closeWindowIfExists();

        new ErrorMessageService().showMessage(exception, false);
        exception.printStackTrace();
    }

    private void closeWindowIfExists() {
        if (window != null) {
            window.close();
        }
    }

    private void updateProjectView(CourseAndExerciseManager courseAndExerciseManager,
                                   ProjectListManager projectListManager) {
        courseAndExerciseManager.setup();
        projectListManager.refreshAllCourses();
    }

    private String getCourseName(String[] exerciseCourse) {
        return exerciseCourse[exerciseCourse.length - 2];
    }

    private String getExerciseName(String[] exerciseCourse) {
        return exerciseCourse[exerciseCourse.length - 1];
    }
}
