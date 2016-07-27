package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsWindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Opens the settings window
 * Defined in plugin.xml on line
 * <action id="Settings" class="fi.helsinki.cs.tmc.intellij.actions.TmcSettingsAction"
 */

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
