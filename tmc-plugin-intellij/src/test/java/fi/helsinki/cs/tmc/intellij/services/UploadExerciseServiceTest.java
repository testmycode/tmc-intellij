package fi.helsinki.cs.tmc.intellij.services;



import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.ExerciseUploadingService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;
import org.junit.Test;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.mockito.Mockito.*;

public class UploadExerciseServiceTest {

    @Test
    public void uploadExercisesTestCallsSubmitMethod() {
        SubmissionResultHandler handler = mock(SubmissionResultHandler.class);
        Map<String, List<Exercise>> map = new HashMap<>();
        CourseAndExerciseManager.setDatabase(map);
        ProjectListManager.setup();

        ObjectFinder finder = mock(ObjectFinder.class);
        CheckForExistingExercises checker = mock(CheckForExistingExercises.class);
        CourseAndExerciseManager manager = mock(CourseAndExerciseManager.class);

        Project project = mock(Project.class);
        TmcCore core = mock(TmcCore.class);
        TmcCoreHolder.set(core);

        Exercise exercise = mock(Exercise.class);
        exercise.setName("user");

        List<Exercise> exerciseList = new ArrayList<>();
        exerciseList.add(exercise);

        Course course = new Course();
        course.setName("home");
        course.setExercises(exerciseList);
        map.put("home", course.getExercises());

        final List<Course> courseList = new ArrayList<>();
        courseList.add(course);

        final SubmissionResult result = mock(SubmissionResult.class);


        when(checker.getListOfDownloadedExercises(course.getExercises()))
                .thenReturn(new ArrayList<Exercise>());
        when(finder.findCourseByName("home", core)).thenReturn(course);
        when(finder.findExerciseByName(course, "user")).thenReturn(exercise);
        when(project.getBasePath()).thenReturn("/home/user");

        when(core.listCourses(ProgressObserver.NULL_OBSERVER)).thenReturn(
                new Callable<List<Course>>() {
                    @Override
                    public List<Course> call() throws Exception {
                        return courseList;
                    }
                }
        );
        when(core.submit(ProgressObserver.NULL_OBSERVER, exercise)).thenReturn(
                new Callable<SubmissionResult>() {
                    @Override
                    public SubmissionResult call() throws Exception {
                        return result;
                    }
                }
        );

        ExerciseUploadingService.startUploadExercise(project, core, finder, checker, handler);
        verify(core).submit(ProgressObserver.NULL_OBSERVER, exercise);
    }
}