package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PathResolverTest {


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getCourseAndExerciseNameWorksCorrectly() throws Exception {

        Project project = mock(Project.class);
        String[] str = {"home", "intellij", "TMCProjects"};
        when(project.getProjectFilePath()).thenReturn(
                "home/intellij/TMCProjects"
        );
        assertArrayEquals(PathResolver.getCourseAndExerciseName(project.getProjectFilePath()),
                str);
    }

}