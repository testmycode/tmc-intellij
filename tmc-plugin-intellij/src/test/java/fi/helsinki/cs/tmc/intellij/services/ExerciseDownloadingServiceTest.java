package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.exercises.CheckForExistingExercises;
import fi.helsinki.cs.tmc.intellij.services.exercises.ExerciseDownloadingService;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

public class ExerciseDownloadingServiceTest {

    /*
    Note that this doesn't really test much actual functionality beyond that the
    ThreadingService is used in some level. At the moment checking for its
    actual functionality is fairly impossible or at least very hard to do
    in any meaningful way (so that it would test also our functionality instead of
    only the TmcCore functioning, which is tested already in its own repository) as
    it would require an actual ThreadingService object (instead of mocking it),
    but calling its 'runWithNotification' method breaks because some IntelliJ's
    openapi stuff isn't possible to produce sensibly within text context.
     */
    @Test
    public void whenDownloadingExercisesThreadingServiceIsUsed() throws Exception {
//        Project project = mock(Project.class);
//        TmcCore core = mock(TmcCore.class);
//        SettingsTmc settings = mock(SettingsTmc.class);
//
//        CheckForExistingExercises checker = mock(CheckForExistingExercises.class);
//        ThreadingService threadingServiceMock = mock(ThreadingService.class);
//        ProgressWindowMaker maker = mock(ProgressWindowMaker.class);
//        ProgressWindow window = mock(ProgressWindow.class);
//        CoreProgressObserver observer = mock(CoreProgressObserver.class);
//        new ExerciseDownloadingService().startDownloadExercise(core, settings, checker, mock(ObjectFinder.class),
//
//                threadingServiceMock, project, true, window, observer);
//
//        verify(threadingServiceMock).runWithNotification(any(Runnable.class), any(Project.class),any(ProgressWindow.class));
    }


}
