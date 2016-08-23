package fi.helsinki.cs.tmc.intellij.importexercise;

import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
    public static void importExercise(String path) throws IOException {
        NewProjectUtilModified.importExercise(path);
    }

    public static boolean hasIdeaFile(String path) {
        logger.info("Check if dir has idea file @ExerciseImport");
        VirtualFile virtualFile = LocalFileSystem.getInstance()
                .refreshAndFindFileByPath(path);
        if (virtualFile == null) {
            logger.warn("Cannot find virtual file matching this path @ExerciseImport");
            return false;
        }

        if (path.endsWith(ProjectFileType.DOT_DEFAULT_EXTENSION)
                || virtualFile.isDirectory()
                        && virtualFile.findChild(Project
                        .DIRECTORY_STORE_FOLDER) != null) {
            return true;
        }
        return false;
    }
}

