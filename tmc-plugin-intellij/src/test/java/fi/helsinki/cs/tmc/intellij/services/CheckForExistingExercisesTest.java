package fi.helsinki.cs.tmc.intellij.services;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import fi.helsinki.cs.tmc.intellij.services.Exercises.CheckForExistingExercises;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class CheckForExistingExercisesTest {

    private CheckForExistingExercises checkForExistingExercises;

    @Before
    public void setUp() throws Exception {
        checkForExistingExercises = new CheckForExistingExercises();
    }

    @Test
    public void cleanWorksCorrectlyIfNothingToRemove() throws Exception {
        SettingsTmc settings = new SettingsTmc();
        Exercise exercise = new Exercise("koira");
        List<Exercise> list = new ArrayList<>();
        list.add(exercise);
        assertEquals(list, checkForExistingExercises.clean(list, settings));
    }

    @Test
    public void cleanWorksCorrectlyIfExerciseAlreadyDownloaded() throws Exception {
        SettingsTmc settings = new SettingsTmc();
        settings.setProjectBasePath("/koira/TMCProjects");
        Path path = settings.getTmcProjectDirectory();
        Exercise exercise = mock(Exercise.class);
        when(exercise.isDownloaded(path)).thenReturn(
                true
        );
        Exercise exercise1 = mock(Exercise.class);
        when(exercise1.isDownloaded(path)).thenReturn(
                false
        );
        List<Exercise> list = new ArrayList<>();
        list.add(exercise);
        list.add(exercise1);
        
        assertEquals(list, checkForExistingExercises.clean(list, settings));
    }

    @Test
    public void getListOfDownloadedExercisesWorksCorrectly() throws Exception {
        SettingsTmc settings = new SettingsTmc();
        Exercise exercise = new Exercise("koira");
        List<Exercise> list = new ArrayList<>();
        list.add(exercise);
        assertTrue(list != checkForExistingExercises.getListOfDownloadedExercises(list, settings));
    }

}