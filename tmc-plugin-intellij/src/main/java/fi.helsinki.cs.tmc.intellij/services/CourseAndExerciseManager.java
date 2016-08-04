package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a database of courses in memory, allowing quick fetching of course
 * when necessary without calling the TmcCore.
 *
 * <p>
 *   Also gives methods to update project list when necessary
 * </p>
 */
public class CourseAndExerciseManager {

    public static void setDatabase(Map<String, List<Exercise>> database) {
        PersistentExerciseDatabase.getInstance().getExerciseDatabase().setCourses(database);
    }

    public CourseAndExerciseManager() {
        try {
            initiateDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setup() {
        try {
            initiateDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Exercise get(String course, String exercise) {
        try {
            List<Exercise> exercises = PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
            for (Exercise exc : exercises) {
                if (exerciseIsTheCorrectOne(exc, exercise)) {
                    return exc;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    private static boolean exerciseIsTheCorrectOne(Exercise exc, String exerciseName) {
        return exc.getName().equals(exerciseName);
    }

    public static List<Exercise> getExercises(String course) {
        try {
            return PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
        } catch (Exception e) {

        }
        return null;
    }

    public static Map<String, List<Exercise>> getDatabase() {
        return PersistentExerciseDatabase.getInstance().getExerciseDatabase().getCourses();
    }

    static void initiateDatabase() throws Exception {
        try {
            Map<String, List<Exercise>> database = new HashMap<>();
            List<Course> courses = (ArrayList<Course>) TmcCoreHolder.get()
                    .listCourses(ProgressObserver.NULL_OBSERVER).call();

            for (Course course : courses) {
                List<Exercise> exercises;
                try {

                    course = TmcCoreHolder.get()
                            .getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                    exercises = (ArrayList<Exercise>) new CheckForExistingExercises()
                            .getListOfDownloadedExercises(course.getExercises(),
                                    TmcSettingsManager.get());
                    database.put(course.getName(), exercises);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            PersistentExerciseDatabase.getInstance().getExerciseDatabase().setCourses(database);
        } catch (TmcCoreException exception) {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception);

            /*Messages.showErrorDialog(new ObjectFinder().findCurrentProject(),
                    exception.getMessage()
                    + " " + exception.toString(), "Error");
            */
        }
    }
    /*
    private void updateDatabase(Course course, List<Exercise> exercises) {
        database.put(course.getName(), exercises);
    }*/


    public static void updateSingleCourse(String courseName, CheckForExistingExercises checker,
                                          ObjectFinder finder,
                                          SettingsTmc settings) {
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
        try {
            initiateDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isCourseInDatabase(String string) {
        return PersistentExerciseDatabase.getInstance()
                .getExerciseDatabase().getCourses().containsKey(string);
    }
}
