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

import java.util.List;

/**
 * Offers method for downloading exercises from selected course.
 */
public class ExerciseDownloadingService {

    public static void startDownloadExercise(final TmcCore core,
                                             final SettingsTmc settings,
                                             final CheckForExistingExercises checker,
                                             ProjectOpener opener) throws Exception {

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

        return new Thread() {
            @Override
            public void run() {
                ObjectFinder finder = new ObjectFinder();
                try {
                    final Course course = finder
                            .findCourseByName(settings.getCourse()
                                    .getName(), core);
                    List<Exercise> exercises = course.getExercises();
                    exercises = checker.clean(exercises, settings);
                    try {
                        core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER,
                                exercises).call();
                    } catch (Exception exception) {
                        ErrorMessageService error = new ErrorMessageService();
                        error.showMessage(exception, "Failed to download exercises.");
                    }

                } catch (Exception except) {
                    ErrorMessageService error = new ErrorMessageService();
                    error.showMessage(except,
                            "You need to select a course to be able to download. \n");
                }

                ApplicationManager.getApplication().invokeLater(
                        new Runnable() {
                            public void run() {
                                ApplicationManager.getApplication().runWriteAction(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    CourseAndExerciseManager.updateAll();
                                                    ProjectListManager.refreshAllCourses();
                                                } catch (Exception exept) {
                                                    exept.printStackTrace();
                                                }
                                            }
                                        }
                                );

                            }
                        });;
            }
        };
    }
}
