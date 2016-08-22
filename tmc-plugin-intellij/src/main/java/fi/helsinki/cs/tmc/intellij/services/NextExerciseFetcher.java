package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.actions.DownloadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import java.util.List;

public class NextExerciseFetcher {

    private String course;
    private Project project;
    private Exercise exercise;

    public NextExerciseFetcher(String course, Exercise exercise, Project project) {
        this.course = course;
        this.exercise = exercise;
        this.project = project;
    }

    public void tryToOpenNext() {
        this.exercise = findNext();
        if (exercise != null) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ProjectOpener().openProject(exercise.getExerciseDirectory(TmcSettingsManager
                            .get().getTmcProjectDirectory()));
                }
            });
        } else {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    SettingsTmc settings = TmcSettingsManager.get();
                    if (Messages.showYesNoDialog("All local exercises for " + course
                                    + " seem to be done! Would you like to try to download the next batch?",
                            "Great work!", null) == 0) {
                        if (!settings.getCourseName()
                                .equals(PathResolver.getCourseName(project.getBasePath()))) {
                            settings.setCourse(PathResolver.getCourse(project.getBasePath()));
                        }
                        new DownloadExerciseAction().downloadExercises(project);
                    }
                }
            });
        }
    }

    public Exercise findNext() {
        CourseAndExerciseManager manager = new CourseAndExerciseManager();
        List<Exercise> exercises = manager.getExercises(course);
        Exercise next = null;
        for (Exercise ex : exercises) {
            if (!ex.isCompleted() && ex.getName().compareTo(exercise.getName()) < 0 && next == null) {
                next = ex;
            } else if (!ex.getName().equals(exercise.getName()) && !ex.isCompleted()) {
                return ex;
            }
        }
        return next;
    }

    public static void openFirst(List<Exercise> exercises) {
        for (Exercise ex: exercises) {
            if (!ex.isCompleted()) {
                new ProjectOpener().openProject(ex
                        .getExerciseDirectory(TmcSettingsManager.get().getTmcProjectDirectory()));
                return;
            }
        }
    }
}
