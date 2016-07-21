package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersistentTmcSettingsTest {

    private PersistentTmcSettings settings;

    @Before
    public void setUp() {
        settings = new PersistentTmcSettings();
    }

    @Test
    public void getStateReturnsCorrectly() throws Exception {
        assertEquals(settings, settings.getState());
    }

    @Test
    public void getSettingsTmcReturnsSettings() throws Exception {
        SettingsTmc set = new SettingsTmc("", "", "");
        settings.setSettingsTmc(set);
        assertEquals(settings.getSettingsTmc(), set);
    }

    @Test
    public void setSettingsTmc() throws Exception {
        SettingsTmc set = settings.getSettingsTmc();
        settings.setSettingsTmc(new SettingsTmc("", "", ""));
        assertNotEquals(set, settings.getSettingsTmc());
    }

}
