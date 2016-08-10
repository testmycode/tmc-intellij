
package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;

import org.jdom.JDOMException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//TODO Loggers are needed here. Also exceptions need refactoring after merging with master.
/**
 * Opens the project using intellij ProjectManager, when given the path.
 */
public class ProjectOpener {

    public void openProject(String path) {
        if (Files.isDirectory(Paths.get(path))) {
            try {
                ProjectManager.getInstance().loadAndOpenProject(path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        } else {
            Messages.showErrorDialog(new ObjectFinder()
                            .findCurrentProject(),
                    "Directory no longer exists", "File not found");
            ProjectListManager.refreshAllCourses();
        }
    }

    public void openProject(Path path) {
        if (Files.isDirectory(path)) {
            try {
                ProjectManager.getInstance().loadAndOpenProject(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        } else {
            Messages.showErrorDialog(new ObjectFinder()
                    .findCurrentProject(),
                    "Directory no longer exists", "File not found");
            ProjectListManager.refreshAllCourses();
        }
    }
}
