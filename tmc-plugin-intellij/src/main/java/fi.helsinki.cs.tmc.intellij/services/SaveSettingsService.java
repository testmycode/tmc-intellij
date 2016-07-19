package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.sun.istack.internal.Nullable;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.intellij.io.Settings;

import java.io.Serializable;
import java.util.ArrayList;


@State(
        name="TmcSettings",
        storages = {
                @Storage("TMCSettings.xml")}
)
public class SaveSettingsService implements PersistentStateComponent<SaveSettingsService> {

    @Nullable
    @Override
    public SaveSettingsService getState() {
        return this;
    }
    @Override
    public void loadState(SaveSettingsService saveSettingsService) {
        XmlSerializerUtil.copyBean(saveSettingsService, this);
    }

    @Nullable
    public static SaveSettingsService getInstance() {
        return ServiceManager.getService(SaveSettingsService.class);
    }


    Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }


}
