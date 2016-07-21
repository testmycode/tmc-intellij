package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;

import java.util.List;

public class ObjectFinder {

    public Exercise findExerciseByName(Course course, String exerciseName) {
        List<Exercise> exercises = course.getExercises();
        for(Exercise exercise: exercises){
            if(exercise.getName().equals(exerciseName)){
                return exercise;
            }
        }
        return null;
    }

    public Course findCourseByName(String courseName, TmcCore core) {
        List<Course> courses = null;
        try {
            courses = core.listCourses(ProgressObserver.NULL_OBSERVER).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                try {
                    return core.getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
