package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExerciseDownloadingServiceTest {

    @Test
    public void downloadExerciseActionWorksAsIntended() throws Exception {
        Project project = mock(Project.class);
        TmcCore core = mock(TmcCore.class);
        Exercise exercise = new Exercise();

        TmcCoreHolder.set(core);

        final List<Exercise> excs = new ArrayList<>();
        excs.add(exercise);
        SettingsTmc settings = mock(SettingsTmc.class);
        settings.setProjectBasePath("/home/koko");
        CheckForExistingExercises checker = mock(CheckForExistingExercises.class);

        when(checker.clean(excs, TmcSettingsManager.get())).thenReturn(excs);

        final Course course = new Course("kurssi");
        when(settings.getCourse()).thenReturn(course);
        course.setExercises(excs);
        final List <Course> courses = new ArrayList<>();
        courses.add(course);
        when(core.listCourses(ProgressObserver.NULL_OBSERVER)).thenReturn(
                new Callable<List<Course>>() {
                    @Override
                    public List<Course> call() throws Exception {
                        return courses;
                    }
                });
        when(settings.getTmcProjectDirectory()).thenReturn(Paths.get("/home/koko"));
        when(core.getCourseDetails(ProgressObserver.NULL_OBSERVER, course)).thenReturn(
                new Callable<Course>() {
                    @Override
                    public Course call() throws Exception {
                        return course;
                    }
                }
        );
        when(checker.getListOfDownloadedExercises(excs, TmcSettingsManager.get())).thenReturn(excs);
        CourseAndExerciseManager.setDatabase(new HashMap<String, ArrayList<Exercise>>());
        ProjectListManager.setCurrentListElements(new HashMap<String, List<JBList>>());
        when(core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER, excs)).thenReturn(
                new Callable<List<Exercise>>() {
                    @Override
                    public List<Exercise> call() throws Exception {
                        return excs;
                    }
                }
        );

        ProjectOpener opener = mock(ProjectOpener.class);
        ExerciseDownloadingService.startDownloadExercise(core, settings, checker, opener);
    }

}
