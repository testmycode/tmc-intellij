package fi.helsinki.cs.tmc.intellij.ui;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.ui.elements.SettingsPanel;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class SettingsPanelLauncher implements Configurable {

    private SettingsPanel settingsUi;
    final PersistentTmcSettings saveSettings = ServiceManager.getService(PersistentTmcSettings.class);

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
        SettingsTmc settingsTmc = saveSettings.getSettingsTmc();
        boolean nullSettings = false;
        if (settingsTmc == null) {
            nullSettings = true;
            settingsTmc = new SettingsTmc("beebee", "bb", "cc");
        }
        settingsTmc.setUsername(settingsUi.getUsernameField().getText());
        settingsTmc.setPassword(settingsUi.getPasswordField().getText());
        settingsTmc.setServerAddress(settingsUi.getServerAddressField().getText());
        settingsTmc.setCourse((Course) settingsUi.getListOfAvailableCourses().getSelectedItem());
        settingsTmc.setProjectBasePath(settingsUi.getProjectPathField().getText());
        saveSettings.setSettingsTmc(settingsTmc);
    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }
}
