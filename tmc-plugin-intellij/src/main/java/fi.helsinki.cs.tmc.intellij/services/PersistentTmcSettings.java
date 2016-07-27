package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;


import com.intellij.util.xmlb.XmlSerializerUtil;

import org.jetbrains.annotations.Nullable;

/**
 * Uses the IDE to save settings on disk
 * Defined in plugin.xml on line in extensions group
 * <applicationService serviceImplementation="fi.helsinki.cs.tmc.intellij.services.PersistentTmcSettings"/>
 */

@State(
        name = "TmcSettings",
        storages = { @Storage("TMCSettings.xml") }
        )

public class PersistentTmcSettings implements PersistentStateComponent<PersistentTmcSettings> {

    @Nullable
    @Override
    public PersistentTmcSettings getState() {
        return this;
    }

    @Override
    public void loadState(PersistentTmcSettings persistentTmcSettings) {
        XmlSerializerUtil.copyBean(persistentTmcSettings, this);
    }

    @Nullable
    public static PersistentTmcSettings getInstance() {
        return ServiceManager.getService(PersistentTmcSettings.class);
    }


    SettingsTmc settingsTmc;

    public SettingsTmc getSettingsTmc() {
        return settingsTmc;
    }

    public void setSettingsTmc(SettingsTmc settingsTmc) {
        this.settingsTmc = settingsTmc;
    }


}
