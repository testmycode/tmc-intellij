package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.ExerciseUploadingService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

/**
 * Uploads the currently active project to TMC Server
 * Defined in plugin.xml on the line
 * &lt;action id="Upload Exercise"
 *   class="fi.helsinki.cs.tmc.intellij.actions.UploadExerciseAction"&gt;
 * Uses CourseAndExerciseManager to update the view after upload,
 * SubmissionResultHandler displays the returned results
 */

public class UploadExerciseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        OperationInProgressNotification note =
                new OperationInProgressNotification("Uploading exercise, please wait!");

        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        ExerciseUploadingService.startUploadExercise(project,
                TmcCoreHolder.get(),
                new ObjectFinder());
        note.hide();
    }

}
