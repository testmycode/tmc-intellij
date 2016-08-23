package fi.helsinki.cs.tmc.intellij.importexercise;

import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

/**
 * Class handles as main tool for imports
 */
public class ExerciseImport {
    /**
     * Handles Exercise import also possibly decides what to import in future.
     * @param path project root dir
     * @throws IOException when writing or reading files.
     */
    public static void importExercise(String path) throws IOException {
        NewProjectUtilModified.importExercise(path);
    }

    public static boolean hasIdeaFile(String path) {
        VirtualFile virtualFile = LocalFileSystem.getInstance()
                .refreshAndFindFileByPath(path);
        if (virtualFile == null) return false;

        if (path.endsWith(ProjectFileType.DOT_DEFAULT_EXTENSION) ||
                virtualFile.isDirectory()
                        && virtualFile.findChild(Project
                        .DIRECTORY_STORE_FOLDER) != null) {
            return true;
        }
        return false;
    }
}

