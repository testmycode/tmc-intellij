package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.project.Project;

import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PathResolverTest {

    @Test
    public void projectPathIsSplitCorrectlyWhenItContainsSlash() throws Exception {

        Project project = mock(Project.class);
        String[] check = {"home", "intellij", "TMCProjects"};
        when(project.getProjectFilePath()).thenReturn(
                "home/intellij/TMCProjects"
        );
        assertArrayEquals(PathResolver.getCourseAndExerciseName(project.getProjectFilePath()),
                check);
    }

    @Test
    public void projectPathIsSplitCorrectlyWhenItContainsBackSlash() {
        String[] check = {"home", "intellij", "TMCProject"};
        String path = "home\\intellij\\TMCProject";

        assertArrayEquals(PathResolver.getCourseAndExerciseName(path), check);
    }

    @Test
    public void tryingToGetCourseAndExerciseInfoReturnsNullIfParameterIsNull() {
        String path = null;
        assertNull(PathResolver.getCourseAndExerciseName(path));
    }

}