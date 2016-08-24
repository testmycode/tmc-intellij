package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;

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

    public static Exercise getExercise(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return CourseAndExerciseManager.get(split[split.length - 2], split[split.length - 1]);
    }

    public static Course getCourse(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return new ObjectFinder().findCourseByName(split[split.length - 2], TmcCoreHolder.get());
    }

    public static String getCourseName(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return split[split.length - 2];
    }

    public static String getExerciseName(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return split[split.length - 1];
    }

    public static String getPathRelativeToProject(String path) {
        String[] paths = path.split(getExerciseName(ObjectFinder.findCurrentProject().getBasePath()));
        return paths[paths.length - 1];
    }
}
