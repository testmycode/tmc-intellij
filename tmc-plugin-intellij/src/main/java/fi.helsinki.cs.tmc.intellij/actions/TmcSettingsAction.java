package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.ui.SettingsWindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class TmcSettingsAction extends AnAction{

    private SettingsWindow window;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        showSettings();
    }

    public void showSettings() {
        if ( window == null || window.isClosed()) {
            window = new SettingsWindow();
        } else {
            window.show();
        }

    }
}
