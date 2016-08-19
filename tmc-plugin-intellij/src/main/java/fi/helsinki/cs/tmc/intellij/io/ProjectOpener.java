
package fi.helsinki.cs.tmc.intellij.io;

import com.intellij.openapi.util.InvalidDataException;
import fi.helsinki.cs.tmc.intellij.importExercise.ExerciseImport;

import com.intellij.openapi.project.ProjectManager;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Opens the project using intellij ProjectManager, when given the path.
 */
public class ProjectOpener {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOpener.class);

    public void openProject(String path) {
        try {
            ExerciseImport.importExercise(path);
               ProjectManager.getInstance().loadAndOpenProject(path);

        } catch(Exception e) {
            System.out.println("VIRHE: " + e.getMessage());
        }
//        logger.info("Opening project from " + path + ". @ProjectOpener");
//        if (Files.isDirectory(Paths.get(path))) {
//            try {
//                ProjectManager.getInstance().loadAndOpenProject(path);
//            } catch (Exception exception) {
//                logger.warn("Could not open project from path. @ProjectOpener",
//                        exception, exception.getStackTrace());
//                new ErrorMessageService().showMessage(exception,
//                        "Could not open project from path. " + path, true);
//            }
//        } else {
//            logger.warn("Directory no longer exists. @ProjectOpener");
//            Messages.showErrorDialog(new ObjectFinder()
//                    .findCurrentProject(),
//                    "Directory no longer exists", "File not found");
//            ProjectListManager.refreshAllCourses();
//        }
    }

    public void openProject(Path path) {
        logger.info("Redirecting openProject with path -> "
                + "openProject with string. @ProjectOpener");
        try {
            ExerciseImport.importExercise(path.toString());
               ProjectManager.getInstance().loadAndOpenProject(path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }
}
