package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.codeEditor.printing.FileSeparatorProvider;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjectFinder {

    public Exercise findExerciseByName(Course course, String exerciseName) {
        List<Exercise> exercises = course.getExercises();

        for (Exercise exercise: exercises) {
            if (exercise.getName().equals(exerciseName)) {
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

    public ArrayList<String> listAllDownloadedCourses() {
        ArrayList<String> fileNames = getListOfDirectoriesInPath(TmcSettingsManager.get().getProjectBasePath());
        return fileNames;
    }

    private ArrayList<String> getListOfDirectoriesInPath(String folderPath) {
        ArrayList<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : directoryStream) {
                String[] exerciseCourse;
                if (path.toString().contains("/")) {
                    exerciseCourse = path.toString().split("/");
                } else {
                    String backslash = "\\\\";
                    exerciseCourse = path.toString().split(backslash);
                }
                fileNames.add(exerciseCourse[exerciseCourse.length - 1]);
            }
        } catch (IOException ex)  {
            ex.printStackTrace();
        }
        return fileNames;
    }

    public ArrayList<String> listAllDownloadedExercises(String courseName) {
        ArrayList<String> fileNames = getListOfDirectoriesInPath(TmcSettingsManager.get().getProjectBasePath()+File.separator+courseName);
        return fileNames;
    }
}
