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
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ObjectFinder.class);

    public Exercise findExerciseByName(Course course, String exerciseName) {
        logger.info("Processing findExerciseByName " + exerciseName + ". @ObjectFinder");
        List<Exercise> exercises = course.getExercises();

        for (Exercise exercise: exercises) {
            if (exercise.getName().equals(exerciseName)) {
                logger.info("Found " + exercise + ". @ObjectFinder");
                return exercise;
            }
        }
        logger.info("Could not find exercise with the name " + exerciseName + ".");
        return null;
    }

    public Course findCourseByName(String courseName, TmcCore core) {
        logger.info("Processing findCourseByName " + courseName + ". @ObjectFinder");
        List<Course> courses = null;
        try {
            courses = core.listCourses(ProgressObserver.NULL_OBSERVER).call();
        } catch (TmcCoreException e) {
            logger.warn("Could not find course " + courseName + ". @ObjectFinder",
                    e, e.getStackTrace());
            new ErrorMessageService().showMessage(e, false);
        } catch (Exception e) {
            logger.warn("Could not find course " + courseName + ". @ObjectFinder",
                    e, e.getStackTrace());
            new ErrorMessageService().showMessage(e,
                    "Could not find course. " + courseName, true);
        }
        if (courses == null) {
            return null;
        }

        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                try {
                    logger.info("Trying to getExercise course details from TmcCore. @ObjectFinder");
                    return core.getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                } catch (TmcCoreException Exception) {
                    logger.warn("Could not find course " + courseName + ". @ObjectFinder",
                            Exception, Exception.getStackTrace());
                    new ErrorMessageService().showMessage(Exception, false);
                } catch (Exception e) {
                    logger.warn("Could not find course " + courseName + ". @ObjectFinder",
                            e, e.getStackTrace());
                    new ErrorMessageService().showMessage(e, "Could not find course.", true);
                }
            }
        }
        return null;
    }

    public List<String> listAllDownloadedCourses() {
        logger.info("Processing listAllDownloadedCourses. @ObjectFinder");
        List<String> fileNames = getListOfDirectoriesInPath(
                TmcSettingsManager.get().getProjectBasePath());
        return fileNames;
    }

    private List<String> getListOfDirectoriesInPath(String folderPath) {
        logger.info("Processing getListOfDirectoriesInPath. @ObjectFinder");
        List<String> fileNames = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream =
                     Files.newDirectoryStream(Paths.get(folderPath))) {
            logger.info("Getting list of directories in path. @ObjectFinder");
            for (Path path : directoryStream) {
                if (!Files.isDirectory(path)) {
                    continue;
                }

                String[] exerciseCourse = PathResolver.getCourseAndExerciseName(path);

                if (exerciseCourse == null
                        || getExerciseName(exerciseCourse).charAt(0) == '.') {
                    logger.info("exerciseCourse variable = null. @ObjectFinder");
                    continue;
                }
                logger.info("Adding exercise " + getExerciseName(exerciseCourse)
                        + "to list. @ObjectFinder");
                fileNames.add(getExerciseName(exerciseCourse));
            }
        } catch (Exception ex)  {
            logger.warn("Could not getExercise list of directories in path. @ObjectFinder",
                    ex, ex.getStackTrace());
            ex.printStackTrace();
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    private String getExerciseName(String[] courseAndExerciseName) {
        logger.info("Trying to getExerciseName. @ObjectFinder");
        return courseAndExerciseName[courseAndExerciseName.length - 1];
    }

    public List<String> listAllDownloadedExercises(String courseName) {
        logger.info("Processing listAllDownloadedExercises from course "
                + courseName + ". @ObjectFinder");
        List<String> fileNames = getListOfDirectoriesInPath(TmcSettingsManager.get()
                .getProjectBasePath() + File.separator + courseName);

        return fileNames;
    }

    public Project findCurrentProject() {
        logger.info("Trying to findCurrentProject. @ObjectFinder");
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        if (dataContext == null) {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            return projects[projects.length - 1];
        }

        Project project = DataKeys.PROJECT.getData(dataContext);

        return project;
    }
}
