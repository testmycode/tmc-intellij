package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import static org.mockito.Mockito.*;

public class UploadExerciseActionTest {

    @Test
    public void uploadExercisesTestCallsSubmitMethod() throws Exception {
        CourseAndExerciseManager.setDatabase(new HashMap<String, ArrayList<Exercise>>());
        ProjectListManager.setup();
        UploadExerciseAction upload = new UploadExerciseAction();
        Project project = mock(Project.class);
        TmcCore core = mock(TmcCore.class);
        Course course = mock(Course.class);
        course.setName("home");
        final List<Course> courseList = new ArrayList<>();
        courseList.add(course);
        Exercise exercise = mock(Exercise.class);
        exercise.setName("user");
        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(exercise);
        course.setExercises(exerciseList);
        final SubmissionResult result = new SubmissionResult();
        TmcCoreHolder.set(core);
        ObjectFinder finder = mock(ObjectFinder.class);
        SubmissionResultHandler handler = mock(SubmissionResultHandler.class);

        when(core.listCourses(ProgressObserver.NULL_OBSERVER)).thenReturn(
                new Callable<List<Course>>() {
                    @Override
                    public List<Course> call() throws Exception {
                        return courseList;
                    }
                }
        );
        when(course.getName()).thenReturn("home");
        when(exercise.getName()).thenReturn("user");
        when(finder.findCourseByName("home", core)).thenReturn(course);
        when(finder.findExerciseByName(course, "user")).thenReturn(exercise);
        when(project.getBasePath()).thenReturn("/home/user");
        when(core.submit(ProgressObserver.NULL_OBSERVER, exercise)).thenReturn(
                new Callable<SubmissionResult>() {
                    @Override
                    public SubmissionResult call() throws Exception {
                        return result;
                    }
                }
        );

        upload.uploadExercise(project,core, finder, handler);
        verify(core).submit(ProgressObserver.NULL_OBSERVER, exercise);
    }
}