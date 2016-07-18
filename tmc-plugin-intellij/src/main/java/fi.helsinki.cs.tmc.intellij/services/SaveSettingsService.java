package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.sun.istack.internal.Nullable;


@State(
        name="TmcSettings",
        storages = {
                @Storage("asdsa.xml")}
)
public class SaveSettingsService implements PersistentStateComponent<SaveSettingsService> {

    @Nullable
    @Override
    public SaveSettingsService getState() {
        System.out.println("get state");
        return this;
    }
    @Override
    public void loadState(SaveSettingsService saveSettingsService) {
        System.out.println("load state");
        XmlSerializerUtil.copyBean(saveSettingsService, this);
    }

    @Nullable
    public static SaveSettingsService getInstance() {
        System.out.println("get instance");
        return ServiceManager.getService(SaveSettingsService.class);
    }

    String password;

    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    String userName;

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }



}
