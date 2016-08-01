package fi.helsinki.cs.tmc.intellij.services;


import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PasteServiceTest {

    private TmcCore core;
    private PasteService service;

    @Before
    public void setUp() throws Exception {
        service = new PasteService();
        core = mock(TmcCore.class);
    }

    @Test
    public void uploadToTmcPastebinCallsPasteWithComment(){
        Exercise exercise = mock(Exercise.class);
        String message = "ahaha.wav";
        service.setCore(core);
        service.setExercise(exercise);
        service.uploadToTmcPastebin(message);
        verify(core).pasteWithComment(ProgressObserver.NULL_OBSERVER, exercise, message);
    }
}
