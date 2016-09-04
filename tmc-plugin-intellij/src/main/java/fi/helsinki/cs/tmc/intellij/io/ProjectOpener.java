
package fi.helsinki.cs.tmc.intellij.io;

import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.importexercise.ExerciseImport;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.spyware.ActivateSpywareListeners;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
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
        openProject(new ObjectFinder().findCurrentProject(), path);
    }

    public void openProject(Project project, String path) {
        logger.info("Opening project from {}. @ProjectOpener", path);
        if (Files.isDirectory(Paths.get(path))) {
            if (project == null
                    || !path.equals(project.getBasePath())) {
                try {
                    if (project != null) {
                        new ActivateSpywareListeners(project).removeListeners();
                    }
                    ExerciseImport.importExercise(path);
                    ProjectUtil.openOrImport(path, project, true);
                    if (project != null) {
                        ProjectManager.getInstance().closeProject(project);
                    }
                } catch (Exception exception) {
                    logger.warn("Could not open project from path. @ProjectOpener",
                            exception, exception.getStackTrace());
                    new ErrorMessageService().showMessage(exception,
                            "Could not open project from path. " + path, true);
                }
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
