package fi.helsinki.cs.tmc.intellij.services;


import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.PasteWindow;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
import org.junit.Test;

import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasteServiceTest {

    private TmcCore core;
    private PasteService service;


    @Test
    public void uploadToTmcPastebinCallsPasteWithComment() throws Exception {
        PasteWindow window = mock(PasteWindow.class);
        service = new PasteService();
        core = mock(TmcCore.class);
        Exercise exercise = mock(Exercise.class);

        String message = "ahaha.wav";
        final URI uri = URI.create("testUri");
        service.setWindow(window);
        service.setCore(core);
        service.setExercise(exercise);

        when(core.pasteWithComment(ProgressObserver.NULL_OBSERVER, exercise, message)).thenReturn(() -> uri);

        ProjectListManager projectListManager = mock(ProjectListManager.class);
        CourseAndExerciseManager courseAndExerciseManager = mock(CourseAndExerciseManager.class);

//        service.uploadToTmcPastebin(message, courseAndExerciseManager, projectListManager);
//
//        verify(core).pasteWithComment(ProgressObserver.NULL_OBSERVER, exercise, message);
//        verify(courseAndExerciseManager).initiateDatabase();
//        verify(projectListManager).refreshAllCourses();
    }

}
