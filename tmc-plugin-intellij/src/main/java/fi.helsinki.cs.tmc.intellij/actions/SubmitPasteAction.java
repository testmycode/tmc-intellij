package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.PasteService;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;


public class SubmitPasteAction extends AnAction{

    private PasteService pasteService;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        paste(anActionEvent);
    }

    private void paste(AnActionEvent anActionEvent) {
        if (pasteService == null || pasteService.getWindow() == null
                || pasteService.getWindow().isClosed()) {
            Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
            pasteService = new PasteService();
            pasteService.uploadExercise(project, TmcCoreHolder.get());
        } else {
            pasteService.getWindow().show();
        }
    }
}
