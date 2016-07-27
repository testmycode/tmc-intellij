package fi.helsinki.cs.tmc.intellij.actions;


import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.CheckForExistingExercises;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Defined in plugin.xml on line
 *  <action id="Download Exercises" class="fi.helsinki.cs.tmc.intellij.actions.DownloadExerciseAction"
 * in group actions
 *
 * Downloads exercises from the course selected in settings,
 * uses CheckForExistingExercises to check already downloaded ones,
 * updates exercise lists with CourseAndExeriseManager
 */
public class DownloadExerciseAction extends AnAction {

    private Exercise exercise;
    private List<Exercise> list;

    public DownloadExerciseAction() {
        exercise = new Exercise();
        list = new ArrayList<Exercise>();
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        OperationInProgressNotification note =
                new OperationInProgressNotification("Downloading exercises, "
                        + "this may take several minutes");
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        try {
            downloadExerciseAction(TmcCoreHolder.get(),
                    TmcSettingsManager.get(),
                    new CheckForExistingExercises(),
                    new ProjectOpener());
        } catch (Exception e) {
            Messages.showMessageDialog(project,
                    "Downloading failed \n"
                            + "Are your account details correct?\n"
                            + e.getMessage(), "Result", Messages.getErrorIcon());
        }
        note.hide();
    }

    public List<Exercise> downloadExerciseAction(TmcCore core,
                                                 SettingsTmc settings,
                                                 CheckForExistingExercises checker,
                                                 ProjectOpener opener) throws Exception {
        ObjectFinder finder = new ObjectFinder();
        Course course = finder.findCourseByName(settings.getCourse().getName(), core);
        List<Exercise> exercises = course.getExercises();
        exercises = checker.clean(exercises);
        core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER, exercises).call();
        CourseAndExerciseManager.updateSinglecourse(course.getName());
        return exercises;
    }
}