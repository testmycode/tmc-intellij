package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

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



}