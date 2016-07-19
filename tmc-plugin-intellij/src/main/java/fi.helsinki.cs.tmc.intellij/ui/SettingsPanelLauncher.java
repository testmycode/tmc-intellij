package fi.helsinki.cs.tmc.intellij.ui;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.intellij.io.Settings;
import fi.helsinki.cs.tmc.intellij.services.SaveSettingsService;
import fi.helsinki.cs.tmc.intellij.ui.elements.SettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsPanelLauncher implements Configurable {

    private SettingsPanel settingsUi;
    final SaveSettingsService saveSettings = ServiceManager.getService(SaveSettingsService.class);

    public SettingsPanelLauncher() {
        settingsUi = new SettingsPanel();
    }

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
        Settings settings = saveSettings.getSettings();
        boolean nullSettings = false;
        if (settings == null) {
            nullSettings = true;
            settings = new Settings("beebee", "bb", "cc");
        }
        settings.setUsername(settingsUi.getUsernameField().getText());
        settings.setPassword(settingsUi.getPasswordField().getText());
        settings.setServerAddress(settingsUi.getServerAddressField().getText());
        settings.setCourse((Course) settingsUi.getListOfAvailableCourses().getSelectedItem());
        settings.setProjectBasePath(settingsUi.getProjectPathField().getText());
        saveSettings.setSettings(settings);
    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }
}
