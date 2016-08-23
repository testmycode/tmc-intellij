package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.commands.GetUpdatableExercises.UpdateResult;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.actions.DownloadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Checks if there are undone and downloadable exercises.
 */
public class CheckForNewExercises {

    private static final Logger logger = LoggerFactory.getLogger(CheckForNewExercises.class);


    public void doCheck() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                logger.info("Checking for new exercises.");
                Project project = new ObjectFinder().findCurrentProject();
                TmcCore core = TmcCoreHolder.get();
                SettingsTmc settings = TmcSettingsManager.get();
                settings.setCourse(new ObjectFinder()
                        .findCourseNoDetails(settings.getCourseName(), core));
                CourseAndExerciseManager manager = new CourseAndExerciseManager();
                try {
                    if (settings.getCourse() != null) {
                        logger.info("Trying to get exercise update data.");
                        UpdateResult result = core
                                .getExerciseUpdates(ProgressObserver.NULL_OBSERVER,
                                settings.getCourse()).call();
                        System.out.println(result.getNewExercises());
                        if (!compareExerciseLists(result.getNewExercises(),
                                manager.getExercises(settings.getCourseName()))) {
                            ErrorMessageService.TMC_NOTIFICATION
                                    .createNotification("New exercises!", "New exercises found for "
                                                    + settings.getCourseName() + ". \n<a href=/>Click here to download them<a>",
                                            NotificationType.INFORMATION,
                                            (notification, hyperlinkEvent) ->
                                                    new DownloadExerciseAction()
                                                    .downloadExercises(project))
                                    .notify(project);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean compareExerciseLists(List<Exercise> newExercises, List<Exercise> exercises) {
        for (Exercise ex : newExercises) {
            if (!findMatchingExercise(ex, exercises)) {
                if (!ex.isCompleted()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean findMatchingExercise(Exercise ex, List<Exercise> exercises) {
        for (Exercise exer : exercises) {
            if (exer.getName().equals(ex.getName())) {
                return true;
            }
        }
        return false;
    }
}
