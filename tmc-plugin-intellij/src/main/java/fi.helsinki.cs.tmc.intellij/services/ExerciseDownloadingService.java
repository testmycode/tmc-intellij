package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.application.ApplicationManager;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Offers method for downloading exercises from selected course.
 */
public class ExerciseDownloadingService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDownloadingService.class);

    public static void startDownloadExercise(final TmcCore core,
                                             final SettingsTmc settings,
                                             final CheckForExistingExercises checker,
                                             ProjectOpener opener) throws Exception {
        logger.info("Preparing to start downloading exercises. @ExerciseDownloadingService");
        Thread run = createThread(core, settings, checker);
        ThreadingService
                .runWithNotification(run,
                        "Downloading exercises, this may take several minutes",
                        new ObjectFinder().findCurrentProject());
    }

    @NotNull
    private static Thread createThread(final TmcCore core,
                                       final SettingsTmc settings,
                                       final CheckForExistingExercises checker) {
        logger.info("Creating a new thread.");
        return new Thread() {
            @Override
            public void run() {
                ObjectFinder finder = new ObjectFinder();
                final Course course = finder
                        .findCourseByName(settings.getCourse()
                                .getName(), core);
                List<Exercise> exercises = course.getExercises();
                exercises = checker.clean(exercises, settings);
                try {
                    logger.info("Starting to download exercise.");
                    core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER,
                            exercises).call();
                } catch (Exception e) {
                    logger.warn("Failed to download exercises", e, e.getStackTrace());
                }
                ApplicationManager.getApplication().invokeLater(
                        new Runnable() {
                            public void run() {
                                ApplicationManager.getApplication().runWriteAction(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                logger.info("Updating project list.");
                                                CourseAndExerciseManager.updateAll();
                                                ProjectListManager.refreshAllCourses();
                                            }
                                        }
                                );
                            }
                        });;
            }
        };
    }
}
