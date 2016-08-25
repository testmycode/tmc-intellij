package fi.helsinki.cs.tmc.intellij.importexercise;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/*
 * Class handles as main tool for imports
 */
public class ExerciseImport {
    private static final Logger logger = LoggerFactory
            .getLogger(NewProjectUtilModified.class);
    /*
     * Handles Exercise import also possibly decides what to import in future.
     * @param path project root dir
     * @throws IOException when writing or reading files.
     */
    public static boolean importExercise(String path) {
        if (isUnImportedNbProject(path)) {
            try {
                NewProjectUtilModified.importExercise(path);
                return true;
            } catch (Exception e) {
                logger.warn("{} @ExerciseImport.importExercise", e);
            }
        }
        return false;
    }

    private static boolean isUnImportedNbProject(String path) {
        logger.info("Check if dir has idea file @ExerciseImport");
        VirtualFile virtualFile = LocalFileSystem.getInstance()
                .refreshAndFindFileByPath(path);
        if (virtualFile == null) {
            logger.warn("Cannot find virtual file matching this path @ExerciseImport");
            return false;
        }
        virtualFile.refresh(false, false);
        if (virtualFile.isDirectory()
                && virtualFile.findChild(Project.DIRECTORY_STORE_FOLDER) == null
                && virtualFile.findChild("nbproject") != null) {
            return true;
        }
        return false;
    }
}

