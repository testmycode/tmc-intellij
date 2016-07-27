package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Finds various exercises and courses from the disk or by
 * asking the TMCServer.
 */

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
        } catch (TmcCoreException e) {
            Messages.showMessageDialog(findCurrentProject(),
                            e.getMessage(), "Error", Messages.getErrorIcon());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                try {
                    return core.getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                } catch (TmcCoreException e) {
                    Messages.showMessageDialog(findCurrentProject(),
                            e.getMessage(), "Error", Messages.getErrorIcon());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public ArrayList<String> listAllDownloadedCourses() {
        ArrayList<String> fileNames = getListOfDirectoriesInPath(
                TmcSettingsManager.get().getProjectBasePath());
        return fileNames;
    }

    private ArrayList<String> getListOfDirectoriesInPath(String folderPath) {
        ArrayList<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream =
                     Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    String[] exerciseCourse;
                    if (path.toString().contains("/")) {
                        exerciseCourse = path.toString().split("/");
                    } else {
                        exerciseCourse = path.toString().split("\\\\");
                    }
                    if (exerciseCourse[exerciseCourse.length - 1].charAt(0) != '.') {
                        fileNames.add(exerciseCourse[exerciseCourse.length - 1]);
                    }
                }
            }
        } catch (IOException ex)  {
            ex.printStackTrace();
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    public ArrayList<String> listAllDownloadedExercises(String courseName) {
        ArrayList<String> fileNames = getListOfDirectoriesInPath(TmcSettingsManager.get()
                .getProjectBasePath() + File.separator + courseName);

        return fileNames;
    }

    public Project findCurrentProject() {
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        Project project = DataKeys.PROJECT.getData(dataContext);
        return project;
    }
}
