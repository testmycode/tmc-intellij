package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseDatabase implements Serializable {

    private Map<String, List<Exercise>> courses;

    public ExerciseDatabase() {
        this.courses =  new HashMap();
    }

    public Map<String, List<Exercise>> getCourses() {
        return courses;
    }

    public void setCourses(Map<String, List<Exercise>> courses) {
        this.courses = courses;
    }
}
