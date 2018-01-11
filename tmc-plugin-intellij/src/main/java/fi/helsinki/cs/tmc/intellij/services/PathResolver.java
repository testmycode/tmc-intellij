package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;

import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Offers methods to get info from a path.
 */
public class PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolver.class);

    public static String[] getCourseAndExerciseName(Project project) {
        logger.info(
                "Processing getCourseAndExerciseName" + " with Project parameter. @PathResolver");
        return getCourseAndExerciseName(project.getBasePath());
    }

    public static String[] getCourseAndExerciseName(Path path) {
        logger.info("Processing getCourseAndExerciseName" + " with Path parameter. @PathResolver");
        return getCourseAndExerciseName(path.toString());
    }

    /*
     * Returns an array containing a course name and exercise name.
     * The course name will be in the second last element of the
     * array and the exercise in the last one.
     */
    public static String[] getCourseAndExerciseName(String path) {
        logger.info(
                "Processing getCourseAndExerciseName " + "with String parameter. @PathResolver");
        if (path == null) {
            return null;
        }

        if (osIsUnixBased(path)) {
            return path.split("/");
        }
        return path.split("\\\\");
    }

    public static Exercise getExercise(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        CourseAndExerciseManager manager = new CourseAndExerciseManager();
        return manager.getExercise(split[split.length - 2], split[split.length - 1]);
    }

    public static Course getCourse(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return new ObjectFinder().findCourse(split[split.length - 2], "name");
    }

    public static String getCourseName(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return split[split.length - 2];
    }

    private static boolean osIsUnixBased(String path) {
        return path.contains("/");
    }

    public static String getExerciseName(String path) {
        if (path == null) {
            return null;
        }
        String[] split = getCourseAndExerciseName(path);
        return split[split.length - 1];
    }

    public String getPathRelativeToProject(String path) {
        Project project = new ObjectFinder().findCurrentProject();
        if (project == null) {
            return null;
        }
        String[] paths =
                path.split(getExerciseName(new ObjectFinder().findCurrentProject().getBasePath()));
        return paths[paths.length - 1];
    }
}
