package fi.helsinki.cs.tmc.intellij.services.exercises;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.commands.GetUpdatableExercises.UpdateResult;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.actions.buttonactions.DownloadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** Checks if there are undone and downloadable exercises. */
public class CheckForNewExercises {

    private static final Logger logger = LoggerFactory.getLogger(CheckForNewExercises.class);

    public void doCheck() {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            logger.info("Checking for new exercises.");
                            Project project = new ObjectFinder().findCurrentProject();
                            TmcCore core = TmcCoreHolder.get();
                            SettingsTmc settings = TmcSettingsManager.get();
                            Course course =
                                    new ObjectFinder()
                                            .findCourseNoDetails(
                                                    settings.getCourseName(), core);

                            if (course == null) {
                                return;
                            }
                            settings.setCourse(course);
                            CourseAndExerciseManager manager = new CourseAndExerciseManager();
                            try {
                                getExerciseUpdateData(project, core, settings, manager);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

    private boolean getExerciseUpdateData(
            Project project, TmcCore core, SettingsTmc settings, CourseAndExerciseManager manager)
            throws Exception {
        logger.info("Trying to get exercise update data.");
        UpdateResult result =
                core.getExerciseUpdates(ProgressObserver.NULL_OBSERVER, settings.getCourse())
                        .call();
        if (newExercisesAreAvailable(
                result.getNewExercises(), manager.getExercises(settings.getCourseName()))) {
            return true;
        }
        createNotificationForNewExercises(project, settings);
        return false;
    }

    private void createNotificationForNewExercises(Project project, SettingsTmc settings) {
        ErrorMessageService.TMC_NOTIFICATION
                .createNotification(
                        "New exercises!",
                        "New exercises found for "
                                + settings.getCourseName()
                                + ". \n<a href=/>Click here to download them<a>",
                        NotificationType.INFORMATION,
                        (notification, hyperlinkEvent) ->
                                new DownloadExerciseAction().downloadExercises(project, false))
                .notify(project);
    }

    private boolean newExercisesAreAvailable(List<Exercise> newExercises, List<Exercise> exercises) {
        return newExercises.stream()
                .filter(ex -> !exerciseIsOnList(ex, exercises))
                .allMatch(Exercise::isCompleted);
    }

    private boolean exerciseIsOnList(Exercise ex, List<Exercise> exercises) {
        return exercises.stream().anyMatch(e -> e.getName().equals(ex.getName()));
    }
}
