package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.CheckForExistingExercises;
import fi.helsinki.cs.tmc.intellij.services.DownloadExercise;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class DownloadExerciseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        OperationInProgressNotification note = new OperationInProgressNotification("Downloading");
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        DownloadExercise downloadExercise = new DownloadExercise();

        try {
            downloadExercise.startDownloadExercise(TmcCoreHolder.get(),
                    TmcSettingsManager.get(),
                    new CheckForExistingExercises(),
                    new ProjectOpener());
        } catch (Exception e) {
            Messages.showMessageDialog(project,
                    "Downloading failed \n" + e.getMessage(), "Result", Messages.getErrorIcon());
        }
        note.hide();
    }
}