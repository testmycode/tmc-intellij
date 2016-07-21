package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UploadExerciseActionTest {


//    @Test
//    public void uploadExercisesTestCallsSubmitMethod() throws Exception {
//        TmcCore core2 = TmcCoreHolder.get();
//        UploadExerciseAction upload = new UploadExerciseAction();
//        Project project = mock(Project.class);
//        TmcCore core = mock(TmcCore.class);
//
//        Course course = new Course();
//        Exercise exercise = new Exercise();
//
//        TmcCoreHolder.set(core2);
//        ObjectFinder finder = mock(ObjectFinder.class);
//        when(finder.findCourseByName("home", core)).thenReturn(course);
//        when(finder.findExerciseByName(course, "user")).thenReturn(exercise);
//        when(project.getBasePath()).thenReturn("/home/user");
//        upload.uploadExercise(project,core2, finder);
//        verify(core2).submit(ProgressObserver.NULL_OBSERVER, exercise);
//    }


}
