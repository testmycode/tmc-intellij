package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    public void setDatabase(Map<String, List<Exercise>> database) {
        getExerciseDatabase().setCourses(database);
    }

    /*
    public CourseAndExerciseManager() {
        try {
            initiateDatabase();
        } catch (Exception e) {
            errorMessageService(e);
        }
    }
    */
    public void setup() {
        try {
            initiateDatabase();
        } catch (Exception e) {
            errorMessageService(e);
        }
    }

    public Exercise get(String course, String exercise) {
        try {
            List<Exercise> exercises = PersistentExerciseDatabase.getInstance()
                    .getExerciseDatabase().getCourses().get(course);
            for (Exercise exc : exercises) {
                if (exerciseIsTheCorrectOne(exc, exercise)) {
                    return exc;
                }
            }
        } catch (Exception exception) {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, "Could not find the exercise");
        }
        return null;
    }

    private boolean exerciseIsTheCorrectOne(Exercise exc, String exerciseName) {
        return exc.getName().equals(exerciseName);
    }

    public List<Exercise> getExercises(String course) {
        try {
            return getDatabase().getCourses().get(course);
        } catch (Exception exception) {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, "Could not find the course.");
        }
        return null;
    }

    public ExerciseDatabase getDatabase() {
        return PersistentExerciseDatabase.getInstance().getExerciseDatabase();
    }

    private void initiateDatabase() throws Exception {
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
                } catch (Exception error) {
                    errorMessageService(error);
                }
            }

            getDatabase().setCourses(database);
        } catch (TmcCoreException exception) {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(exception, false);

            refreshCoursesOffline();
        }
    }

    private void refreshCoursesOffline() {
        Map<String, List<Exercise>> courses = getExerciseDatabase().getCourses();

        for (String courseName : courses.keySet()) {
            List<Exercise> exercises = courses.get(courseName);

            removeExercisesNotFoundFromLocalDirectories(exercises, courseName);
        }
        getExerciseDatabase().setCourses(courses);
    }

    private void removeExercisesNotFoundFromLocalDirectories(List<Exercise> exercises,
                                                                    String courseName) {

        List<String> exerciseNamesThroughDirectories =
                getExerciseNamesThroughDirectories(courseName);

        Iterator<Exercise> iterator = exercises.iterator();

        while (iterator.hasNext()) {
            Exercise exercise = iterator.next();

            if (!exerciseNamesThroughDirectories.contains(exercise.getName())) {
                iterator.remove();
            }
        }
    }

    private List<String> getExerciseNamesThroughDirectories(String courseName) {
        return new ObjectFinder().listAllDownloadedExercises(courseName);
    }

    private ExerciseDatabase getExerciseDatabase() {
        return PersistentExerciseDatabase.getInstance().getExerciseDatabase();
    }

    /*
    private void updateDatabase(Course course, List<Exercise> exercises) {
        database.put(course.getName(), exercises);
    }*/


    public void updateSingleCourse(String courseName, CheckForExistingExercises checker,
                                          ObjectFinder finder,
                                          SettingsTmc settings) {

        boolean isNewCourse = getExerciseDatabase().getCourses().get(courseName) == null;
        Course course = finder.findCourseByName(courseName, TmcCoreHolder.get());

        if (course == null) {
            return;
        }

        List<Exercise> existing = (ArrayList<Exercise>) checker
                .getListOfDownloadedExercises(course.getExercises(), settings);

        getExerciseDatabase().getCourses().put(courseName, existing);

        if (isNewCourse) {
            ProjectListManagerHolder.get().refreshAllCourses();
        } else {
            ProjectListManagerHolder.get().refreshCourse(courseName);
        }
    }

    public void updateAll() {
        try {
            initiateDatabase();
        } catch (Exception e) {
            errorMessageService(e);
        }
    }

    private static void errorMessageService(Exception exception) {
        ErrorMessageService error = new ErrorMessageService();
        error.showMessage(exception, "Could not initiate database.");
        exception.printStackTrace();
    }

    public static boolean isCourseInDatabase(String string) {
        return PersistentExerciseDatabase.getInstance()
                .getExerciseDatabase().getCourses().containsKey(string);
    }
}
