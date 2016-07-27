package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.project.Project;

/**
 * Offers methods to get info from a path.
 */
public class PathResolver {

    /**
     * Returns a given project's course and exercise name in
     * an array.
     * @param project project info is wanted from
     * @return array containing the course and exercise name derived
     *     from a project's path
     */
    public static String[] getCourseAndExerciseName(Project project) {
        String path = project.getBasePath();

        if (path == null) {
            return null;
        }

        if (osIsUnixBased(path)) {
            return path.split("/");
        }
        return path.split("\\\\");

    }

    private static boolean osIsUnixBased(String path) {
        return path.contains("/");
    }
}
