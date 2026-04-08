package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains the TmcSettings. */
public final class TmcSettingsManager {

    private static final Logger logger = LoggerFactory.getLogger(TmcSettingsManager.class);

    private TmcSettingsManager() {}

    private static PersistentTmcSettings persistentSettings() {
        return PersistentTmcSettings.getInstance();
    }

    public static synchronized SettingsTmc get() {
        logger.info("Get SettingsTmc. @TmcSettingsManager.");
        PersistentTmcSettings ps = persistentSettings();
        if (ps.getSettingsTmc() == null) {
            ps.setSettingsTmc(new SettingsTmc());
        }
        return ps.getSettingsTmc();
    }

    public static synchronized void setup() {
        logger.info("Setup SettingsTmc. @TmcSettingsManager.");
        PersistentTmcSettings ps = persistentSettings();
        if (ps.getSettingsTmc() == null) {
            ps.setSettingsTmc(new SettingsTmc());
        }
    }
}
