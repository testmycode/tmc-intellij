package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.actions.DownloadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.CheckForExistingExercises;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DownloadExerciseActionTest {

    @Test
    public void actionPerformed() throws Exception {

    }

    @Test
    public void downloadExerciseActionWorksAsIntended() throws Exception {
        DownloadExerciseAction download = new DownloadExerciseAction();
        Project project = mock(Project.class);
        TmcCore core = mock(TmcCore.class);
        Exercise exercise = new Exercise();

        TmcCoreHolder.set(core);

        final List<Exercise> excs = new ArrayList<>();
        excs.add(exercise);
        SettingsTmc settings = mock(SettingsTmc.class);
        settings.setProjectBasePath("/home/koko");
        CheckForExistingExercises checker = mock(CheckForExistingExercises.class);

        when(checker.clean(excs)).thenReturn(excs);

        final Course course = new Course("kurssi");
        when(settings.getCourse()).thenReturn(course);
        course.setExercises(excs);

        when(settings.getTmcProjectDirectory()).thenReturn(Paths.get("/home/koko"));
        when(core.getCourseDetails(ProgressObserver.NULL_OBSERVER, course)).thenReturn(
                new Callable<Course>() {
                    @Override
                    public Course call() throws Exception {
                        return course;
                    }
                }
        );

        when(core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER, excs)).thenReturn(
                new Callable<List<Exercise>>() {
                    @Override
                    public List<Exercise> call() throws Exception {
                        return excs;
                    }
                }
        );

        ProjectOpener opener = mock(ProjectOpener.class);
        download.downloadExerciseAction(core, settings, checker, opener);
    }


}