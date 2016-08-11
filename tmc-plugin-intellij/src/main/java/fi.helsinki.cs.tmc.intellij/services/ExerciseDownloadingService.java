package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
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

    public void startDownloadExercise(final TmcCore core,
                                      final SettingsTmc settings,
                                      final CheckForExistingExercises checker,
                                      ObjectFinder objectFinder,
                                      ErrorMessageService errorMessageService) throws Exception {

        Thread run = createThread(core, settings, checker, objectFinder, errorMessageService);
        ThreadingService
                .runWithNotification(run,
                        "Downloading exercises, this may take several minutes",
                        objectFinder.findCurrentProject());
    }

    @NotNull
    private Thread createThread(final TmcCore core,
                               final SettingsTmc settings,
                               final CheckForExistingExercises checker,
                               final ObjectFinder finder,
                               final ErrorMessageService errorMessageService) {

        return new Thread() {
            @Override
            public void run() {
                try {
                    final Course course = finder
                            .findCourseByName(settings.getCourse()
                                    .getName(), core);
                    List<Exercise> exercises = course.getExercises();
                    exercises = checker.clean(exercises, settings);
                    System.out.println("exercises are: " + exercises);
                    try {
                        core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER,
                                exercises).call();
                    } catch (Exception exception) {
                        errorMessageService.showMessage(exception, "Failed to download exercises.");
                    }

                } catch (Exception except) {
                    errorMessageService.showMessage(except,
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
                                                    new CourseAndExerciseManager().updateAll();
                                                    ProjectListManagerHolder.get()
                                                            .refreshAllCourses();
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
