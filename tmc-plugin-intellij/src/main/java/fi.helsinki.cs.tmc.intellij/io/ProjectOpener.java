
package fi.helsinki.cs.tmc.intellij.io;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.InvalidDataException;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import org.jdom.JDOMException;

import java.io.IOException;
import java.nio.file.Path;

public class ProjectOpener {

    public ProjectOpener() {
    }

    public void openProject(String path) {
        try {
            ProjectManager.getInstance().loadAndOpenProject(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    public void openProject(Path path) {
        try {
            ProjectManager.getInstance().loadAndOpenProject(path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    public void openPreviousExercise(String path) {

    }

    public void openNextExercise(String path) {

    }

    public void openNextExercise(Course course, Exercise exercise) {

    }
}
