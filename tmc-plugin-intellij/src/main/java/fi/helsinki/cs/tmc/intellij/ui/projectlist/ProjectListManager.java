package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

/**
 * Contains the ProjectListWindow.
 */
public class ProjectListManager {

    private static final Logger logger = LoggerFactory.getLogger(ProjectListManager.class);
    private static Map<String, List<JBList>> currentListElements;
    private static List<ProjectListWindow> projectListWindows;

    public ProjectListManager() {
        logger.info("Setting up ProjectListWindow. @ProjectListManager");
        projectListWindows = new ArrayList<>();
        currentListElements = new HashMap<>();
    }

    public void addList(JBList list) {
        logger.info("Processing addList. @ProjectListManager");
        if (currentListElements.get(list.getName()) == null) {
            currentListElements.put(list.getName(), new ArrayList<JBList>());
        }
        currentListElements.get(list.getName()).add(list);
    }

    public void refreshAllCourses() {
        logger.info("Refreshing all courses. @ProjectListManager");
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                for (ProjectListWindow window : projectListWindows) {
                    window.addCourseTabsAndExercises();
                }
            }
        });
    }

    public void refreshCourse(String course) {
        logger.info("Refreshing course {}. @ProjectListManager", course);
        List<JBList> list = currentListElements.get(course);
        if (list == null) {
            return;
        }

        for (JBList jbList : list) {
            if (jbList == null || !jbList.getName().equals(course)) {
                continue;
            }
            DefaultListModel model = (DefaultListModel) jbList.getModel();
            model.removeAllElements();
            addExercisesToList(new ObjectFinder(), course, model, new CourseAndExerciseManager());
            jbList.setModel(model);
        }
        refreshAllCourses();
    }

    public void addExercisesToList(ObjectFinder finder,
                                          String course, DefaultListModel defaultListModel,
                                          CourseAndExerciseManager courseAndExerciseManager) {

        logger.info("Processing addExercisesToList. @ProjectListManager");
        if (courseAndExerciseManager.isCourseInDatabase(course)) {
            List<Exercise> exercises = courseAndExerciseManager.getExercises(course);
            addExercisesToListModel(defaultListModel, exercises);
        } else {
            List<String> exercises = finder.listAllDownloadedExercises(course);
            addExercisesToListModelAsStrings(defaultListModel, exercises);
        }
    }

    private void addExercisesToListModel(DefaultListModel listModel,
                                                List<Exercise> exercises) {
        logger.info("Processing addExercisesToListModel. @ProjectListManager");
        for (Exercise ex : exercises) {
            listModel.addElement(ex);
        }
    }

    private void addExercisesToListModelAsStrings(DefaultListModel listModel,
                                                         List<String> exercises) {
        logger.info("Processing addExercisesToListModelAsStrings. @ProjectListManager");
        for (String ex : exercises) {
            listModel.addElement(ex);
        }
    }

    public void addWindow(ProjectListWindow window) {
        projectListWindows.add(window);
    }

}
