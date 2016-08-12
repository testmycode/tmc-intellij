package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

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
    static List<ProjectListWindow> projectListWindows;

    public ProjectListManager() {
        logger.info("Setting up ProjectListWindow. @ProjectListManager");
        projectListWindows = new ArrayList<>();
        currentListElements = new HashMap<>();
    }

    public static void setup() {
        logger.info("Setup ProjectListWindow. @ProjectListManager");
        if (currentListElements == null) {
            currentListElements = new HashMap<>();
            projectListWindows = new ArrayList<>();
        }
    }

    public static void addList(JBList list) {
        if (currentListElements.get(list.getName()) == null) {
            currentListElements.put(list.getName(), new ArrayList<JBList>());
        }
        currentListElements.get(list.getName()).add(list);
    }

    public static void refreshAllCourses() {
        logger.info("Refreshing all courses. @ProjectListManager");
        for (ProjectListWindow window : projectListWindows) {
            window.addCourseTabsAndExercises();
        }
    }


    public static void refreshCourse(String course) {
        logger.info("Refreshing course " + course + ". @ProjectListManager");
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
            addExercisesToList(new ObjectFinder(), course, model);
            jbList.setModel(model);
        }
        refreshAllCourses();
    }

    public static void addExercisesToList(ObjectFinder finder,
                                          String course, DefaultListModel defaultListModel) {
        logger.info("Adding exercises to list for future use. @ProjectListManager");
        if (CourseAndExerciseManager.isCourseInDatabase(course)) {
            List<Exercise> exercises = CourseAndExerciseManager.getExercises(course);
            addExercisesToListModel(defaultListModel, exercises);
        } else {
            List<String> exercises = finder.listAllDownloadedExercises(course);
            addExercisesToListModelAsStrings(defaultListModel, exercises);
        }
    }

    private static void addExercisesToListModel(DefaultListModel listModel,
                                                List<Exercise> exercises) {
        for (Exercise ex : exercises) {
            listModel.addElement(ex);
        }
    }

    private static void addExercisesToListModelAsStrings(DefaultListModel listModel,
                                                         List<String> exercises) {
        for (String ex : exercises) {
            listModel.addElement(ex);
        }
    }

    public static void setCurrentListElements(HashMap<String, List<JBList>> currentListElements) {
        ProjectListManager.currentListElements = currentListElements;
    }

    public static void addWindow(ProjectListWindow window) {
        projectListWindows.add(window);
    }

}
