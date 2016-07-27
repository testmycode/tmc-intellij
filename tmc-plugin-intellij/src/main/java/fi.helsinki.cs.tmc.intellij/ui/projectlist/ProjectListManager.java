package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.ui.components.JBList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

public class ProjectListManager {

    static HashMap<String, List<JBList>> currentListElements;
    static ArrayList<JPanel> panelList;

    public ProjectListManager() {
        panelList = new ArrayList<>();
        currentListElements = new HashMap<>();
    }

    public static void setup() {
        if (currentListElements == null) {
            currentListElements = new HashMap<>();
            panelList = new ArrayList<>();
        }
    }

    public static void addList(JBList list) {
        if (currentListElements.get(list.getName()) == null) {
            currentListElements.put(list.getName(), new ArrayList<JBList>());
        }
        currentListElements.get(list.getName()).add(list);
    }

    public static void refreshAllCourses() {
        List<String> courses = new ObjectFinder().listAllDownloadedCourses();
        for (String course : courses) {
            refreshCourse(course);
        }
    }

    public static void refreshCourse(String course) {
        List<JBList> list = currentListElements.get(course);
        if (list != null) {
            for (JBList lis : list) {
                if (lis != null && lis.getName().equals(course)) {
                    DefaultListModel model = (DefaultListModel) lis.getModel();
                    model.removeAllElements();
                    addExercisesToList(new ObjectFinder(), course, model);
                    lis.setModel(model);
                }
            }
        }
    }

    public static void addExercisesToList(ObjectFinder finder,
                                          String course, DefaultListModel defaultListModel) {
        if (CourseAndExerciseManager.isCourseInDatabase(course)) {
            ArrayList<Exercise> exercises = CourseAndExerciseManager.getExercises(course);
            for (Exercise ex : exercises) {
                defaultListModel.addElement(ex);
            }
        } else {
            ArrayList<String> exercises = finder.listAllDownloadedExercises(course);
            for (String ex : exercises) {
                defaultListModel.addElement(ex);
            }
        }
    }

    public static void setCurrentListElements(HashMap<String, List<JBList>> currentListElements) {
        ProjectListManager.currentListElements = currentListElements;
    }
}
