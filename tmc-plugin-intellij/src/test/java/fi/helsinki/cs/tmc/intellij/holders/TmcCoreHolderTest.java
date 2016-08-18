package fi.helsinki.cs.tmc.intellij.holders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import fi.helsinki.cs.tmc.core.TmcCore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



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
