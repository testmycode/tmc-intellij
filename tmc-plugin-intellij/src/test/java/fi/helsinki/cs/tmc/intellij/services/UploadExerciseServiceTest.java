package fi.helsinki.cs.tmc.intellij.services;



import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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

        new ExerciseUploadingService().startUploadExercise(project, mock(TmcCore.class), finder, checker,
                mock(SubmissionResultHandler.class), settings,
                mockCourseAndExerciseManager, threadingService);

        verify(threadingService, never()).runWithNotification(any(Runnable.class), anyString(), any(Project.class));
        verify(mockCourseAndExerciseManager, never()).updateSingleCourse(course.getName(),
                checker, finder, settings);
    }
}