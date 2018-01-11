package fi.helsinki.cs.tmc.intellij.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.google.common.base.Optional;
import com.intellij.openapi.components.ServiceManager;
import fi.helsinki.cs.tmc.core.domain.OauthCredentials;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import org.junit.Before;
import org.junit.Test;



public class PersistentTmcSettingsTest {

    private PersistentTmcSettings savedSettings;
    private SettingsTmc settingsTmc;

    @Before
    public void setUp() {
        settingsTmc = new SettingsTmc("kissa", "koira", "kana");
        settingsTmc.setOrganization(Optional.of(new Organization("Default", "default", "default", "default", false)));
        settingsTmc.setOauthCredentials(Optional.of(new OauthCredentials("kirra", "koisa")));
        settingsTmc.setToken(Optional.of("kocakola"));

        PersistentTmcSettings persistentSettings = new PersistentTmcSettings();
        persistentSettings.setSettingsTmc(settingsTmc);

        savedSettings = new PersistentTmcSettings(); // dunno if this really works as expected
        savedSettings.loadState(persistentSettings);
    }

    @Test
    public void getStateReturnsCorrectly() throws Exception {
        assertEquals(savedSettings, savedSettings.getState());
    }

    @Test
    public void getSettingsTmcReturnsSettings() throws Exception {
        assertEquals(savedSettings.getSettingsTmc(), settingsTmc);
    }

    @Test
    public void setSettingsTmcWorks() throws Exception {
        SettingsTmc set = savedSettings.getSettingsTmc();
        savedSettings.setSettingsTmc(new SettingsTmc("", "", ""));
        assertNotEquals(set, savedSettings.getSettingsTmc()); // checks that tmc settings have changed
    }
}
