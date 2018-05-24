package fi.helsinki.cs.tmc.intellij.actions.buttonactions;

import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.ui.login.LoginDialog;
import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsWindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opens the settings window. Defined in plugin.xml on line &lt;action id="Settings"
 * class="fi.helsinki.cs.tmc.intellij .actions.buttonactions.TmcSettingsAction"&gt;
 */
public class TmcSettingsAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(TmcSettingsAction.class);
    private SettingsWindow window;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.info("Performing TmcSettingsAction. @TmcSettingsAction");
        showSettings();
    }

    public void showSettings() {
        logger.info("Opening TMC setting window. @TmcSettingsAction");
        if (TmcSettingsManager.get().getToken().isPresent()) {
            if (window == null || window.isClosed()) {
                window = new SettingsWindow();
            } else {
                window.show();
            }
        } else {
            LoginDialog.display();
        }
    }
}
