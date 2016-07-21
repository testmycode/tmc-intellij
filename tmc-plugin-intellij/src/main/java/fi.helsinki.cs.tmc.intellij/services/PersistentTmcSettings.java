package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;


@State(
        name="TmcSettings",
        storages = {
                @Storage("TMCSettings.xml")}
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


    SettingsTmc SettingsTmc;

    public SettingsTmc getSettingsTmc() {
        return SettingsTmc;
    }

    public void setSettingsTmc(SettingsTmc settingsTmc) {
        this.SettingsTmc = settingsTmc;
    }

}
