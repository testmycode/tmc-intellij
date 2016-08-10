package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.ui.Messages;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
/**
 * Holds a database of courses in memory, allowing quick fetching of course
 * when necessary without calling the TmcCore.
 *
 * <p>
 *   Also gives methods to update project list when necessary
 * </p>
 */
public class CourseAndExerciseManager {

    private static final Logger logger = LoggerFactory.getLogger(CourseAndExerciseManager.class);

    public static void setDatabase(Map<String, List<Exercise>> database) {
        PersistentExerciseDatabase.getInstance().getExerciseDatabase().setCourses(database);
    }

    public CourseAndExerciseManager() {
        logger.info("CourseAndExerciseManager constructor call.");
            initiateDatabase();
    }

    public static void setup() {
        logger.info("Setup CourseAndExerciseManager.");
            initiateDatabase();
    }

    public static Exercise get(String course, String exercise) {
        logger.info("Get exercise from CourseAndExerciseManager.");
        try {
            List<Exercise> exercises = PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
            for (Exercise exc : exercises) {
                if (exerciseIsTheCorrectOne(exc, exercise)) {
                    return exc;
                }
            }
        } catch (Exception exception) {
            logger.warn("Exercise was not found.", exception, exception.getStackTrace());
            exception.printStackTrace();
        }
        return null;
    }

    private static boolean exerciseIsTheCorrectOne(Exercise exc, String exerciseName) {
        logger.info("Checking if " + exc.getName() + " equals " + exerciseName + ".");
        return exc.getName().equals(exerciseName);
    }

    public static List<Exercise> getExercises(String course) {
        logger.info("Getting list of exercises.");
        try {
            return PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
        } catch (Exception e) {
            logger.warn("Course was not found", e, e.getStackTrace());
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, List<Exercise>> getDatabase() {
        logger.info("Get database from CourseAndExerciseManager.");
        return PersistentExerciseDatabase.getInstance().getExerciseDatabase().getCourses();
    }

    static void initiateDatabase() {
        logger.info("Initiating database. Fetching courses from TmcCore.");
        try {
            Map<String, List<Exercise>> database = new HashMap<>();
            List<Course> courses = (ArrayList<Course>) TmcCoreHolder.get()
                    .listCourses(ProgressObserver.NULL_OBSERVER).call();

            for (Course course : courses) {
                List<Exercise> exercises;
                try {
                    logger.info("Initiating database.");
                    course = TmcCoreHolder.get()
                            .getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                    exercises = (ArrayList<Exercise>) new CheckForExistingExercises()
                            .getListOfDownloadedExercises(course.getExercises(),
                                    TmcSettingsManager.get());
                    database.put(course.getName(), exercises);
                } catch (Exception e) {
                    logger.warn("Failed to initiate database.", e, e.getStackTrace());
                    e.printStackTrace();
                }
            }

            PersistentExerciseDatabase.getInstance().getExerciseDatabase().setCourses(database);
        } catch (TmcCoreException exception) {
            logger.warn("Failed to fetch courses from TmcCore.", exception, exception.getStackTrace());
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception);
            /*Messages.showErrorDialog(new ObjectFinder().findCurrentProject(),
                    exception.getMessage()
                    + " " + exception.toString(), "Error");
            */
        } catch (Exception exception) {
            logger.warn("Failed to fetch courses from TmcCore.",
                    exception, exception.getStackTrace());
            Messages.showErrorDialog(exception.getCause().getMessage()
                    + " Failed to fetch courses from TmcCore. ", exception.toString());
            //TODO After merging with master, implement this to use ErrorMessageService.
        }
    }
    /*
    private void updateDatabase(Course course, List<Exercise> exercises) {
        database.put(course.getName(), exercises);
    }*/


    public static void updateSingleCourse(String courseName, CheckForExistingExercises checker,
                                          ObjectFinder finder,
                                          SettingsTmc settings) {
        logger.info("Updating single course");
        boolean isNewCourse = PersistentExerciseDatabase.getInstance()
                .getExerciseDatabase().getCourses().get(courseName) == null;
        Course course = finder.findCourseByName(courseName, TmcCoreHolder.get());

        List<Exercise> existing = (ArrayList<Exercise>) checker
                .getListOfDownloadedExercises(course.getExercises(), settings);

        PersistentExerciseDatabase.getInstance().getExerciseDatabase()
                .getCourses().put(courseName, existing);

        if (isNewCourse) {
            ProjectListManager.refreshAllCourses();
        } else {
            ProjectListManager.refreshCourse(courseName);
        }
    }

    public static void updateAll() {
        logger.info("UpdateAll call CourseAndExerciseManager");
        initiateDatabase();
    }

    public static boolean isCourseInDatabase(String string) {
        return PersistentExerciseDatabase.getInstance()
                .getExerciseDatabase().getCourses().containsKey(string);
    }
}
