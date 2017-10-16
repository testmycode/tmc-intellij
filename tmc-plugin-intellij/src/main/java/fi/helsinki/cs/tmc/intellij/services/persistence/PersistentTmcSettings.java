package fi.helsinki.cs.tmc.intellij.services.persistence;

import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

import com.intellij.util.xmlb.XmlSerializerUtil;

import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses the IDE to save settings on disk
 * Defined in plugin.xml on line in extensions group
 * <applicationService serviceImplementation
 *   ="fi.helsinki.cs.tmc.intellij.services.Persistence.PersistentTmcSettings"/>
 */

@State(
        name = "TmcSettings",
        storages = { @Storage("TMCSettings.xml") }
        )
public class PersistentTmcSettings implements PersistentStateComponent<PersistentTmcSettings> {


    private static final Logger logger = LoggerFactory.getLogger(PersistentTmcSettings.class);
    private SettingsTmc settingsTmc;

    @Nullable
    @Override
    public PersistentTmcSettings getState() {
        logger.info("Processing getState. @PersistentTmcSettings.");
        return this;
    }

    @Override
    public void loadState(PersistentTmcSettings persistentTmcSettings) {
        logger.info("Processing loadState. @PersistentTmcSettings.");
        XmlSerializerUtil.copyBean(persistentTmcSettings, this);
    }

    @Nullable
    public static PersistentTmcSettings getInstance() {
        logger.info("Processing getInstance. @PersistentTmcSettings");
        return ServiceManager.getService(PersistentTmcSettings.class);
    }

    public SettingsTmc getSettingsTmc() {
        logger.info("Getting SettingsTmc. @PersistentTmcSettings.");
        return settingsTmc;
    }

    public void setSettingsTmc(SettingsTmc settingsTmc) {
        logger.info("Setting SettingsTmc. @PersistentTmcSettings.");
        this.settingsTmc = settingsTmc;
    }


}
