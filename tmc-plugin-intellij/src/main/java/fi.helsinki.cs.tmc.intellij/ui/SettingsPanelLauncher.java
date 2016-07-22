package fi.helsinki.cs.tmc.intellij.ui;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.ui.elements.SettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

//not used
public class SettingsPanelLauncher implements Configurable {

    private SettingsPanel settingsUi;
    final PersistentTmcSettings saveSettings = ServiceManager.getService(PersistentTmcSettings.class);

    public SettingsPanelLauncher() {
       // settingsUi = new SettingsPanel();
    }


    public SettingsPanel getSettingsUi() { return settingsUi; }

    @Nls
    @Override
    public String getDisplayName() {
        return "TMC Settings";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "Tmc Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return settingsUi.getPanel();
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        SettingsTmc SettingsTmc = saveSettings.getSettingsTmc();
        boolean nullSettings = false;
        if (SettingsTmc == null) {
            nullSettings = true;
            SettingsTmc = new SettingsTmc("beebee", "bb", "cc");
        }
        SettingsTmc.setUsername(settingsUi.getUsernameField().getText());
        SettingsTmc.setPassword(settingsUi.getPasswordField().getText());
        SettingsTmc.setServerAddress(settingsUi.getServerAddressField().getText());
        SettingsTmc.setCourse((Course) settingsUi.getListOfAvailableCourses().getSelectedItem());
        SettingsTmc.setProjectBasePath(settingsUi.getProjectPathField().getText());
        saveSettings.setSettingsTmc(SettingsTmc);
    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }
}
