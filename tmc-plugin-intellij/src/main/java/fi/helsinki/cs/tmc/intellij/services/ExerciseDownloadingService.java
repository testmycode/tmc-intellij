package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.exercisedownloadlist.DownloadListWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
                                             boolean downloadAll,
                                             ProgressWindow window) throws Exception {

        logger.info(
                "Preparing to start checking for available exercises."
                        + "@ExerciseDownloadingService");
        Thread run = checkAvailableExercises(core, settings, checker,
                objectFinder, downloadAll);
        threadingService.runWithNotification(
                run,
                project,
                window);
    }

    public static void startDownloading(List<Exercise> exercises) {
        logger.info(
                "Preparing to start downloading exercises. @ExerciseDownloadingService");
        ProgressWindow window = ProgressWindowMaker.make(
                "Downloading exercises, this may take a while",
                new ObjectFinder().findCurrentProject(), true, true, false);
        CoreProgressObserver observer = new CoreProgressObserver(window);
        Thread run = downloadSelectedExercises(TmcCoreHolder.get(), exercises, observer);
        ThreadingService threadingService = new ThreadingService();
        Project project = new ObjectFinder().findCurrentProject();

        threadingService.runWithNotification(
                run,
                project,
                window);
    }

    private static Thread checkAvailableExercises(final TmcCore core,
                                                  final SettingsTmc settings,
                                                  final CheckForExistingExercises checker,
                                                  final ObjectFinder finder,
                                                  boolean downloadAll) {

        logger.info(
                "Creating a new thread to check available exercises. @ExerciseDownloadingService");

        return new Thread() {
            @Override
            public void run() {
                ErrorMessageService errorMessageService = new ErrorMessageService();
                try {
                    logger.info("Starting to check exercises. @ExerciseDownloadingService");

                    final Course course = finder.findCourseByName(settings.getCourse().getName(),
                            core);

                    List<Exercise> exercises = course.getExercises();
                    exercises = checker.clean(exercises, settings);
                    if (!downloadAll) {
                        exercises = doneFilter(exercises);
                    }
                    if (exercises == null || exercises.size() == 0) {
                        new ErrorMessageService().downloadErrorMessage(course);
                        return;
                    }
                    new DownloadListWindow().showDownloadableExercises(exercises);
                } catch (Exception except) {
                    logger.warn("Failed to check available exercises. "
                                    + "Course not selected. @ExerciseDownloadingService",
                            except, except.getStackTrace());
                    errorMessageService.showMessage(except,
                            "You need to select a course to be able to download.", true);
                }
            }
        };
    }

    @NotNull
    private static Thread downloadSelectedExercises(final TmcCore core,
                                                    final List<Exercise> exercises,
                                                    CoreProgressObserver observer) {

        logger.info("Creating a new thread. @ExerciseDownloadingService");

        return new Thread() {
            @Override
            public void run() {
                try {
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
                } catch (Exception exception) {
                    logger.info("Failed to download exercises. @ExerciseDownloadingService");
                    new ErrorMessageService().showMessage(exception,
                            "Failed to download exercises.", true);
                }

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
                        });
            }
        };
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

    private static List<Exercise> doneFilter(List<Exercise> exercises) {
        ArrayList<Exercise> filtered = new ArrayList<>();
        for (Exercise ex : exercises) {
            if (!ex.isCompleted()) {
                filtered.add(ex);
            }
        }
        return filtered;
    }
}
