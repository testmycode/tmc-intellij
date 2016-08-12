package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the courses. Used by ExerciseDatabaseManager to set and get the course map.
 */
public class ExerciseDatabase implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDatabase.class);
    private Map<String, List<Exercise>> courses;

    public ExerciseDatabase() {
        this.courses =  new HashMap();
    }

    public Map<String, List<Exercise>> getCourses() {
        logger.info("Get courses from ExerciseDatabase.");
        return courses;
    }

    public void setCourses(Map<String, List<Exercise>> courses) {
        logger.info("Set courses at ExerciseDatabase.");
        this.courses = courses;
    }
}
