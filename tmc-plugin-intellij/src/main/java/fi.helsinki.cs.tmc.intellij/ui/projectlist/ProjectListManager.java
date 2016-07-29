package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.ui.components.JBList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class ProjectListManager {

    private static Map<String, List<JBList>> currentListElements;
    static List<ProjectListWindow> projectListWindows;

    public ProjectListManager() {
        projectListWindows = new ArrayList<>();
        currentListElements = new HashMap<>();
    }

    public static void setup() {
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
        for (ProjectListWindow window : projectListWindows) {
            window.addCourseTabsAndExercises();
        }
    }

    public static void refreshCourse(String course) {
        List<JBList> list = currentListElements.get(course);
        if (list == null) {
            return;
        }

        for (JBList jbList : list) {
            if (list == null || !jbList.getName().equals(course)) {
                continue;
            }
            DefaultListModel model = (DefaultListModel) jbList.getModel();
            model.removeAllElements();
            addExercisesToList(new ObjectFinder(), course, model);
            jbList.setModel(model);
        }
    }

    public static void addExercisesToList(ObjectFinder finder,
                                          String course, DefaultListModel defaultListModel) {

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
