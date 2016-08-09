package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.PasteWindow;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.net.URI;

public class PasteService {

    PasteWindow window;
    Exercise exercise;
    TmcCore core;

    public void showSubmitForm(Project project, TmcCore core) {
        String[] exerciseCourse = PathResolver.getCourseAndExerciseName(project);

        this.exercise = CourseAndExerciseManager.get(exerciseCourse[exerciseCourse.length - 2],
                exerciseCourse[exerciseCourse.length - 1]);
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

    public void uploadToTmcPastebin(String message) {
        try {
            URI uri = core.pasteWithComment(ProgressObserver.NULL_OBSERVER,
                    exercise, message).call();
            window.showResult(uri);
//            updateProjectView();
        } catch (TmcCoreException exception) {
            handleException(exception);
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Uknown error", "Error while uploading to TMC Pastebin");
            updateProjectView();
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

    private void updateProjectView() {
        CourseAndExerciseManager.updateAll();
        ProjectListManager.refreshAllCourses();
    }
}
