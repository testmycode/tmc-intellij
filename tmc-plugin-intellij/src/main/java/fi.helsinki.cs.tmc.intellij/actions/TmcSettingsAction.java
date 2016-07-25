package fi.helsinki.cs.tmc.intellij.actions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fi.helsinki.cs.tmc.intellij.ui.SettingsWindow;

public class TmcSettingsAction extends AnAction{

    SettingsWindow window;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        showSettings();
    }

    public void showSettings() {
        if ( window == null || window.isClosed()){
            window = new SettingsWindow();
        } else {
            window.show();
        }

    }
}
