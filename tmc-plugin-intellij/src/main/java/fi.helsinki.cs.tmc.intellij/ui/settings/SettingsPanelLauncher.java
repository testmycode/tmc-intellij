package fi.helsinki.cs.tmc.intellij.ui.settings;

//not used
/*
public class SettingsPanelLauncher implements Configurable {
    private SettingsPanel settingsUi;
    final PersistentTmcSettings saveSettings =
            ServiceManager.getService(PersistentTmcSettings.class);
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
*/