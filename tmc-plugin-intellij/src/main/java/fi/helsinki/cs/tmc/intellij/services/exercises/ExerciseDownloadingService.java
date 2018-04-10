package fi.helsinki.cs.tmc.intellij.services.exercises;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.ProgressWindowMaker;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.ui.exercisedownloadlist.DownloadListWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/** Offers method for downloading exercises from selected course. */
public class ExerciseDownloadingService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDownloadingService.class);

    public static void startDownloadExercise(
            final TmcCore core,
            final SettingsTmc settings,
            final CheckForExistingExercises checker,
            ObjectFinder objectFinder,
            ThreadingService threadingService,
            Project project,
            boolean downloadAll,
            ProgressWindow window) {

        logger.info(
                "Preparing to start checking for available exercises."
                        + "@ExerciseDownloadingService");
        Thread run = checkAvailableExercises(core, settings, checker, objectFinder, downloadAll);
        threadingService.runWithNotification(run, project, window);
    }

    public static void startDownloading(List<Exercise> exercises) {
        logger.info("Preparing to start downloading exercises. @ExerciseDownloadingService");
        ProgressWindow window =
                ProgressWindowMaker.make(
                        "Downloading exercises, this may take a while",
                        new ObjectFinder().findCurrentProject(),
                        true,
                        true,
                        false);
        CoreProgressObserver observer = new CoreProgressObserver(window);
        Thread run = downloadSelectedExercises(TmcCoreHolder.get(), exercises, observer);
        ThreadingService threadingService = new ThreadingService();
        Project project = new ObjectFinder().findCurrentProject();
        threadingService.runWithNotification(run, project, window);
    }

    private static Thread checkAvailableExercises(
            final TmcCore core,
            final SettingsTmc settings,
            final CheckForExistingExercises checker,
            final ObjectFinder finder,
            boolean downloadAll) {

        logger.info(
                "Creating a new thread to check available exercises. @ExerciseDownloadingService");

        return new Thread(() -> {
            try {
                logger.info("Starting to check exercises. @ExerciseDownloadingService");

                final Course course =
                        finder.findCourse(settings.getCurrentCourse().get().getName(), "name");

                List<Exercise> exercises = course.getExercises();
                exercises = checker.clean(exercises, settings);
                if (!downloadAll) {
                    exercises = notCompletedExercises(exercises);
                }
                if (exercises == null || exercises.size() == 0) {
                    new ErrorMessageService().showExercisesAreUpToDate(course);
                    return;
                }
                new DownloadListWindow().showDownloadableExercises(exercises);

            } catch (Exception except) {
                logger.warn(
                        "Failed to check available exercises. "
                                + "Course not selected. @ExerciseDownloadingService",
                        except,
                        except.getStackTrace());
                new ErrorMessageService()
                        .showErrorMessageWithExceptionDetails(
                                except,
                                "You need to select a course to be able to download.",
                                true);
            }
        });
    }

    @NotNull
    private static Thread downloadSelectedExercises(
            final TmcCore core, final List<Exercise> exercises, CoreProgressObserver observer) {

        logger.info("Creating a new thread. @ExerciseDownloadingService");

        return new Thread(() -> {
            try {
                List<Exercise> exerciseList =
                        core.downloadOrUpdateExercises(observer, exercises).call();
                ApplicationManager.getApplication()
                        .invokeLater(
                                () -> {
                                    if (0
                                            == Messages.showYesNoDialog(
                                                    "Would you like to open the first "
                                                     + "of the downloaded exercises?",
                                            "Download Complete",
                                                    null)) {
                                        NextExerciseFetcher.openFirst(exerciseList);
                                    }
                                });
            } catch (Exception exception) {
                logger.info("Failed to download exercises. @ExerciseDownloadingService");
                new ErrorMessageService()
                        .showErrorMessageWithExceptionDetails(exception, "Failed to download exercises.", true);
            }

            createThreadForRefreshingExerciseList();
        });
    }

    private static boolean handleCreatingThread(
            ObjectFinder finder,
            SettingsTmc settings,
            TmcCore core,
            CheckForExistingExercises checker,
            CoreProgressObserver observer) {
        logger.info("Starting to download exercise. @ExerciseDownloadingService");

        final Course course = finder.findCourse(settings.getCurrentCourse().get().getName(), "name");

        List<Exercise> exercises = course.getExercises();
        exercises = checker.clean(exercises, settings);
        if (exercises == null || exercises.size() == 0) {
            new ErrorMessageService().showExercisesAreUpToDate(course);
            return true;
        }
        try {
            openFirstExercise(exercises, core, observer);
        } catch (Exception exception) {
            logger.info("Failed to download exercises. @ExerciseDownloadingService");
            new ErrorMessageService().showErrorMessageWithExceptionDetails(exception, "Failed to download exercises.", true);
        }
        return false;
    }

    private static void openFirstExercise(
            List<Exercise> exercises, TmcCore core, CoreProgressObserver observer)
            throws Exception {
        List<Exercise> exerciseList = core.downloadOrUpdateExercises(observer, exercises).call();
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            if (0
                                    == Messages.showYesNoDialog(
                                            "Would you like to open the first "
                                                    + "of the downloaded exercises?",
                                    "Download Complete",
                                            null)) {
                                NextExerciseFetcher.openFirst(exerciseList);
                            }
                        });
    }

    private static void createThreadForRefreshingExerciseList() {
        logger.info("Creating new thread for refreshing exerciseList");
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> ApplicationManager.getApplication()
                                .runWriteAction(
                                        () -> {
                                            logger.info(
                                                    "Updating project list. "
                                                    + "@ExerciseDownloadingService");
                                            refreshExerciseList();
                                        }));
    }

    private static void refreshExerciseList() {
        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> {
                            new CourseAndExerciseManager().initiateDatabase();
                            ApplicationManager.getApplication()
                                    .invokeLater(
                                            () -> ProjectListManagerHolder.get()
                                                    .refreshAllCourses());
                        });
    }

    private static List<Exercise> notCompletedExercises(List<Exercise> exercises) {
        return exercises.stream()
                .filter(e -> !e.isCompleted())
                .collect(Collectors.toList());
    }
}
