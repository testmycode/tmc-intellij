package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.project.Project;

public class PathResolver {

    public String[] getStrings(Project project) {
        String path = project.getBasePath();
        String[] exerciseCourse = new String[2];
        if (path != null) {
            if (path.contains("/")) {
                exerciseCourse = path.split("/");
            } else {
                String backslash = " \\ ";
                backslash = backslash.trim();
                exerciseCourse = path.split(backslash);
            }
        }
        return exerciseCourse;
    }
}
