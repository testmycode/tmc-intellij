package fi.helsinki.cs.tmc.intellij.actions.buttonactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fi.helsinki.cs.tmc.intellij.ui.aboutTmc.AboutTmcDialog;


public class ShowAboutTmcAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        AboutTmcDialog.display();
    }
}
