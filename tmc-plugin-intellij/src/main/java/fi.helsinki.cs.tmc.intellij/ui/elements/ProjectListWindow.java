package fi.helsinki.cs.tmc.intellij.ui.elements;

import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

public class ProjectListWindow {

    private JTabbedPane tabbedPanelBase;

    public JPanel getBasePanel() {
        return basePanel;
    }

    private JPanel basePanel;
    private JToolBar toolbar;

    public ProjectListWindow() {
        addCourseTabsAndExercises();
    }

    private void addCourseTabsAndExercises() {
        ObjectFinder finder = new ObjectFinder();
        List<String> courses = finder.listAllDownloadedCourses();
        final ProjectOpener opener = new ProjectOpener();
        for (String course : courses) {
            JBScrollPane panel = new JBScrollPane();
            final JBList list = new JBList();
            DefaultListModel defaultListModel = new DefaultListModel();

            panel.setBorder(BorderFactory.createTitledBorder(""));

            List<String> exercises = finder.listAllDownloadedExercises(course);
            for (String ex : exercises) {
                defaultListModel.addElement(ex);
            }
            list.setModel(defaultListModel);

            MouseListener mouseListener = mouseAdapter(opener, list);
            list.addMouseListener(mouseListener);

            panel.setName(course);
            panel.setViewportView(list);

            tabbedPanelBase.addTab(course, panel);
        }
    }

    private MouseAdapter mouseAdapter(final ProjectOpener opener, final JBList list) {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {

                if (event.getClickCount() >= 2) {
                    String selectedItem = (String) list.getSelectedValue();
                    opener.openProject(
                            TmcSettingsManager.get().getProjectBasePath()
                                    + File.separator
                                    + list.getParent().getParent().getName()
                                    + File.separator
                                    + selectedItem);
                }
            }
        };
    }

    {
        setupUi();
    }

    private void setupUi() {
        basePanel = new JPanel();
        basePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPanelBase = new JTabbedPane();

        basePanel.add(tabbedPanelBase, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null,
                new Dimension(200, 200),
                null,
                0,
                false));

        toolbar = new JToolBar();
        toolbar.setFloatable(false);

        basePanel.add(toolbar, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                new Dimension(-1, 20),
                null,
                0,
                false));
    }

    /**
     * @noinspection ALL.
     */
    public JComponent getRootComponent() {
        return basePanel;
    }
}
