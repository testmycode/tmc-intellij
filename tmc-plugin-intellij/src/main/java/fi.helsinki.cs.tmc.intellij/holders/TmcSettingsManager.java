package fi.helsinki.cs.tmc.intellij.holders;

import com.intellij.openapi.components.ServiceManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.PersistentTmcSettings;


public final class TmcSettingsManager {


    private static final PersistentTmcSettings persistentSettings = ServiceManager.getService(PersistentTmcSettings.class);

    private TmcSettingsManager() {
    }

    public static synchronized SettingsTmc get() {
        if (persistentSettings.getSettingsTmc() == null) {
            persistentSettings.setSettingsTmc(new SettingsTmc("", "", ""));
        }
        return persistentSettings.getSettingsTmc();
    }

    public static synchronized void setup() {
        if (persistentSettings.getSettingsTmc() == null) {
            persistentSettings.setSettingsTmc(new SettingsTmc("", "", ""));
        }
    }

    public static synchronized void set(SettingsTmc settings) {
        persistentSettings.setSettingsTmc(settings);
    }
}