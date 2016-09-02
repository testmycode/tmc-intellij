package fi.helsinki.cs.tmc.intellij.services.Exercises;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import fi.helsinki.cs.tmc.intellij.services.Errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Offers method for downloading exercises from selected course.
 */
public class ExerciseDownloadingService {

    private static final Logger logger = LoggerFactory
            .getLogger(ExerciseDownloadingService.class);

    public static void startDownloadExercise(final TmcCore core,
                                             final SettingsTmc settings,
                                             final CheckForExistingExercises checker,
                                             ObjectFinder objectFinder,
                                             ThreadingService threadingService,
                                             Project project,
                                             ProgressWindow window,
                                             CoreProgressObserver observer) throws Exception {

        logger.info("Preparing to start downloading exercises. @ExerciseDownloadingService");
        Thread run = createThread(core, settings, checker, objectFinder, observer);
        threadingService.runWithNotification(
                run,
                project,
                window);
    }

    @NotNull
    private static Thread createThread(final TmcCore core,
                                       final SettingsTmc settings,
                                       final CheckForExistingExercises checker,
                                       final ObjectFinder finder,
                                       CoreProgressObserver observer) {

        logger.info("Creating a new thread. @ExerciseDownloadingService");

        return new Thread() {
            @Override
            public void run() {
                try {
                    if (handleCreatingThread(finder, settings, core, checker, observer)) {
                        return;
                    }
                } catch (Exception except) {
                    logger.warn("Failed to download exercises. "
                                    + "Course not selected. @ExerciseDownloadingService",
                            except, except.getStackTrace());
                    new ErrorMessageService().showMessage(except,
                            "You need to select a course to be able to download.", true);
                }
                createThreadForRefreshingExerciseList();
            }
        };
    }



    private static boolean handleCreatingThread(ObjectFinder finder,
                                                SettingsTmc settings,
                                                TmcCore core,
                                                CheckForExistingExercises checker,
                                                CoreProgressObserver observer) {
        logger.info("Starting to download exercise. @ExerciseDownloadingService");

        final Course course = finder.findCourseByName(settings.getCourse().getName(),
                core);

        List<Exercise> exercises = course.getExercises();
        exercises = checker.clean(exercises, settings);
        if (exercises == null || exercises.size() == 0) {
            new ErrorMessageService().downloadErrorMessage(course);
            return true;
        }
        try {
            openFirstExercise(exercises, core, observer);
        } catch (Exception exception) {
            logger.info("Failed to download exercises. @ExerciseDownloadingService");
            new ErrorMessageService().showMessage(exception,
                    "Failed to download exercises.", true);
        }
        return false;
    }

    private static void openFirstExercise(List<Exercise> exercises,
                                          TmcCore core,
                                          CoreProgressObserver observer) throws Exception {
        List<Exercise> exerciseList = core
                .downloadOrUpdateExercises(observer,
                exercises).call();
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (0 == Messages
                        .showYesNoDialog("Would you like to open the first "
                                        + "of the downloaded exercises?",
                        "Download complete", null)) {
                    NextExerciseFetcher.openFirst(exerciseList);
                }
            }
        });
    }

    private static void createThreadForRefreshingExerciseList() {
        logger.info("Creating new thread for refreshing exerciseList");
        ApplicationManager.getApplication().invokeLater(
                new Runnable() {
                    public void run() {
                        ApplicationManager.getApplication().runWriteAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        logger.info("Updating project list. "
                                                + "@ExerciseDownloadingService");
                                        refreshExerciseList();
                                    }
                                }
                        );
                    }
                }
        );
    }

    private static void refreshExerciseList() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                new CourseAndExerciseManager().initiateDatabase();
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ProjectListManagerHolder.get().refreshAllCourses();
                    }
                });
            }
        });
    }
}
