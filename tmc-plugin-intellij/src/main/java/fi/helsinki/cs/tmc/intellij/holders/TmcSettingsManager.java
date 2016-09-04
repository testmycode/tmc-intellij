package fi.helsinki.cs.tmc.intellij.holders;


import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;

import com.intellij.openapi.components.ServiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the TmcSettings.
 */
public final class TmcSettingsManager {

    private static final Logger logger = LoggerFactory.getLogger(TmcSettingsManager.class);
    private static final PersistentTmcSettings persistentSettings =
            ServiceManager.getService(PersistentTmcSettings.class);

    private TmcSettingsManager() {

    }

    public static synchronized SettingsTmc get() {
        logger.info("Get SettingsTmc. @TmcSettingsManager.");
        if (persistentSettings.getSettingsTmc() == null) {
            persistentSettings.setSettingsTmc(new SettingsTmc());
        }
        return persistentSettings.getSettingsTmc();
    }

    public static synchronized void setup() {
        logger.info("Setup SettingsTmc. @TmcSettingsManager.");
        if (persistentSettings.getSettingsTmc() == null) {
            persistentSettings.setSettingsTmc(new SettingsTmc());
        }
    }
}