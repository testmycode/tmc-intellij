package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.project.Project;

import java.nio.file.Path;

/**
 * Offers methods to get info from a path.
 */
public class PathResolver {

    /**
     * Returns an array containing a course name and exercise name.
     * The course name will be in the second last element of the
     * array and the exercise in the last one.
     * @param project project info is wanted from.
     * @return array containing the course and exercise name derived
     *     from a project's path
     */
    public static String[] getCourseAndExerciseName(Project project) {
        return getCourseAndExerciseName(project.getBasePath());

    }

    /**
     * Returns an array containing a course name and exercise name.
     * The course name will be in the second last element of the
     * array and the exercise in the last one.
     * @param path path of a project the info is wanted from
     * @return array containing the course and exercise name
     *     derived from the given path
     */
    public static String[] getCourseAndExerciseName(Path path) {
        return getCourseAndExerciseName(path.toString());
    }

    /**
     * Returns an array containing a course name and exercise name.
     * The course name will be in the second last element of the
     * array and the exercise in the last one.
     * @param path path of a project as string
     * @return array containing the course and exercise name derived
     *     from the given path
     */
    public static String[] getCourseAndExerciseName(String path) {
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
