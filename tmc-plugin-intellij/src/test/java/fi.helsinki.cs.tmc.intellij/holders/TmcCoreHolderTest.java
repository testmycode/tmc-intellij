package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;

import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.PersistentTmcSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TmcCoreHolderTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getReturnsCoreWithoutSetup() throws Exception {
        TmcCore core = TmcCoreHolder.testGet();
        assertNotEquals(core, null);
    }

    @Test
    public void setupDoesNothingWhenCalledAfterCreatingCore() throws Exception {
        TmcCore core = TmcCoreHolder.testGet();
        TmcCoreHolder.setup();
        assertEquals(TmcCoreHolder.get(), core);
    }

    @Test
    public void getReturnsCoreAfterItHasBeenAssigned() throws Exception {
        TmcCore core = TmcCoreHolder.testGet();
        TmcCoreHolder.setup();
        assertEquals(TmcCoreHolder.get(), core);
    }

}