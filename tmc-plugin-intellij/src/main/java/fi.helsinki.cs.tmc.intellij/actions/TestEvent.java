package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.ui.SubmissionResultPopup;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class TestEvent extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
//        Messages.showMessageDialog(project, "Testing " + project.getName() + " " + project.getProjectFilePath() , "Information", Messages.getInformationIcon());
        Messages.showErrorDialog(new SubmissionResultPopup().getPanel1(), "lol");
    }
}
