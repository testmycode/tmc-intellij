package fi.helsinki.cs.tmc.intellij.ui.courseselection;

import com.google.common.base.Optional;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.ui.settings.SettingsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseListWindow extends JPanel {

    private static JFrame frame;
    private final JBList<Course> courses;
    private static JButton button;

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public CourseListWindow(List<Course> courses) {
        Course[] courseArray = courses.toArray(new Course[courses.size()]);
        this.courses = new JBList<>(courseArray);
        this.courses.setFixedCellHeight(107);
        this.courses.setFixedCellWidth(346);


        this.courses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.button = new JButton("Select");
        button.addActionListener(new SelectCourseListener(this));

        this.courses.setCellRenderer(new CourseCellRenderer());
        this.courses.setVisibleRowCount(4);
        JScrollPane pane = new JBScrollPane(this.courses);
        Dimension d = pane.getPreferredSize();
        d.width = 800;
        d.height = (int) (d.height * 1.12);
        pane.setPreferredSize(d);
        pane.setBorder(new EmptyBorder(5, 0, 5, 0));
        pane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        pane.getVerticalScrollBar().setUnitIncrement(10);

        this.courses.setBackground(new Color(242, 241, 240));

        this.courses.setSelectedIndex(setDefaultSelectedIndex());
        this.courses.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() >= 2) {
                        button.doClick();
                    }
                }
            });
        add(pane);
        add(button);
    }

    public static void display() throws Exception {
        logger.info("Showing Course list window. @CourseListWindow");

        if (frame == null) {
            frame = new JFrame("Select a course");
        }
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<Course> courses =
            TmcCoreHolder.get().listCourses(ProgressObserver.NULL_OBSERVER).call();

        final CourseListWindow courseListWindow = new CourseListWindow(courses);
        frame.setContentPane(courseListWindow);

        if (hasCourses(courses)) {
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        }

        button.setMinimumSize(new Dimension(courseListWindow.getWidth(), button.getHeight()));
        button.setMaximumSize(new Dimension(courseListWindow.getWidth(), button.getHeight()));

        courseListWindow.addComponentListener(
            new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent event) {
                    button.setMinimumSize(
                        new Dimension(courseListWindow.getWidth(), button.getHeight()));
                    button.setMaximumSize(
                        new Dimension(courseListWindow.getWidth(), button.getHeight()));
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                }

                @Override
                public void componentShown(ComponentEvent e) {
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                }
            });
    }

    public static boolean isWindowVisible() {
        if (frame == null) {
            return false;
        }
        return frame.isVisible();
    }

    private static boolean hasCourses(List<Course> courses) throws Exception {
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(
                frame, "Organization has no courses!", "Error", JOptionPane.ERROR_MESSAGE);
            frame.setVisible(false);
            frame.dispose();
            return false;
        }
        return true;
    }

    private int setDefaultSelectedIndex() {
        final Optional<Course> currentCourse = TmcSettingsHolder.get().getCurrentCourse();
        if (!currentCourse.isPresent()) {
            return 0;
        }
        String selectedCourseName = currentCourse.get().getName();

        final ListModel<Course> list = courses.getModel();
        for (int i = 0; i < list.getSize(); i++) {
            if (list.getElementAt(i).getName().equals(selectedCourseName)) {
                return i;
            }
        }

        return 0;
    }

    class SelectCourseListener implements ActionListener {

        private final Logger logger = LoggerFactory.getLogger(LoginManager.class);

        public SelectCourseListener(CourseListWindow window) {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Action SelectCourse performed. @SelectCourseListener");

            final Course course = courses.getSelectedValue();
            frame.setVisible(false);
            frame.dispose();

            try {
                final PersistentTmcSettings saveSettings =
                    ServiceManager.getService(PersistentTmcSettings.class);
                SettingsTmc settingsTmc =
                    ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();

                settingsTmc.setCourse(Optional.of(course));
                saveSettings.setSettingsTmc(settingsTmc);

                if (SettingsPanel.getInstance() != null) { // Update SettingsPanel if it's visible
                    SettingsPanel.getInstance().setCurrentCourse();
                }
                ProjectListManagerHolder.get().refreshAllCourses();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

class CourseCellRenderer extends DefaultListCellRenderer {

    private static final Color HIGHLIGHT_COLOR = new Color(240, 119, 70);

    private final Map<Course, CourseCard> cachedCourses;

    public CourseCellRenderer() {
        this.cachedCourses = new HashMap<>();
    }

    @Override
    public Component getListCellRendererComponent(
            final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean hasFocus) {

        final Course course = (Course) value;
        if (!this.cachedCourses.containsKey(course)) {
            this.cachedCourses.put(course, new CourseCard(course));
        }
        CourseCard courseCard = this.cachedCourses.get(course);

        if (isSelected) {
            courseCard.setColors(Color.white, HIGHLIGHT_COLOR);
        } else {
            courseCard.setColors(new Color(76, 76, 76), Color.white);
        }
        return courseCard;
    }
}
