package fi.helsinki.cs.tmc.intellij.services;



import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

public class UploadExerciseServiceTest {

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
    public void afterUploadingExercisesTheCourseIsUpdated() {
        ObjectFinder finder = mock(ObjectFinder.class);
        CheckForExistingExercises checker = mock(CheckForExistingExercises.class);
        SettingsTmc settings = mock(SettingsTmc.class);
        ThreadingService threadingService = mock(ThreadingService.class);
        Course course = new Course("home");

        Project project = mock(Project.class);
        when(project.getBasePath()).thenReturn("/home/user");

        CourseAndExerciseManager mockCourseAndExerciseManager = mock(CourseAndExerciseManager.class);
        when(mockCourseAndExerciseManager.isCourseInDatabase(course.getName())).thenReturn(true);

        new ExerciseUploadingService().startUploadExercise(project, mock(TmcCore.class), finder, checker,
                mock(SubmissionResultHandler.class), settings,
                mockCourseAndExerciseManager, threadingService);

        verify(threadingService).runWithNotification(any(Runnable.class), anyString(), any(Project.class));
        verify(mockCourseAndExerciseManager).updateSingleCourse(course.getName(),
                checker, finder, settings);
    }


    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // commented out for now, works in IntelliJ, but fails in Maven,
    // runs into ExceptionInInitializerError along with
    // java.util.MissingResourceException: Can't find bundle for base name messages.CommonBundle, locale en_US
    /*
    @Test
    public void submittingFailsIfExerciseIsNotTmcExercise() {
        ObjectFinder finder = mock(ObjectFinder.class);
        CheckForExistingExercises checker = mock(CheckForExistingExercises.class);
        SettingsTmc settings = mock(SettingsTmc.class);
        ThreadingService threadingService = mock(ThreadingService.class);
        Course course = new Course("home");

        Project project = mock(Project.class);
        when(project.getBasePath()).thenReturn("/home/user");

        CourseAndExerciseManager mockCourseAndExerciseManager = mock(CourseAndExerciseManager.class);
        when(mockCourseAndExerciseManager.isCourseInDatabase(course.getName())).thenReturn(false);
        SubmissionResultHandler handler = new SubmissionResultHandler();

        exception.expect(IllegalStateException.class);
        new ExerciseUploadingService().startUploadExercise(project, mock(TmcCore.class), finder, checker,
                handler, settings,
                mockCourseAndExerciseManager, threadingService);

        verify(threadingService, never()).runWithNotification(any(Runnable.class), anyString(), any(Project.class));
        verify(mockCourseAndExerciseManager, never()).updateSingleCourse(course.getName(),
                checker, finder, settings);
    }
    */
}