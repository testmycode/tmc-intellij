
package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.services.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Opens the project using intellij ProjectManager, when given the path.
 */
public class ProjectOpener {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOpener.class);

    public void openProject(String path) {
        logger.info("Opening project from {}. @ProjectOpener", path);
        if (Files.isDirectory(Paths.get(path))) {
            try {
                ProjectManager.getInstance().loadAndOpenProject(path);
            } catch (Exception exception) {
                logger.warn("Could not open project from path. @ProjectOpener",
                        exception, exception.getStackTrace());
                new ErrorMessageService().showMessage(exception,
                        "Could not open project from path. " + path, true);
            }
        } else {
            logger.warn("Directory no longer exists. @ProjectOpener");
            Messages.showErrorDialog(new ObjectFinder()
                    .findCurrentProject(),
                    "Directory no longer exists", "File not found");
            ProjectListManagerHolder.get().refreshAllCourses();
        }
    }

    public void openProject(Path path) {
        logger.info("Redirecting openProject with path -> "
                + "openProject with string. @ProjectOpener");
        openProject(path.toString());
    }
}
