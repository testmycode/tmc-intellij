package fi.helsinki.cs.tmc.intellij.ui.elements;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

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
        ArrayList<String> courses = finder.listAllDownloadedCourses();
        final ProjectOpener opener = new ProjectOpener();
        for (String course : courses) {
            JBScrollPane panel = new JBScrollPane();
            final JBList list = new JBList();
            DefaultListModel defaultListModel = new DefaultListModel();
            panel.setBorder(BorderFactory.createTitledBorder(""));
            ArrayList<String> exercises = finder.listAllDownloadedExercises(course);
            for (String ex : exercises) {
                defaultListModel.addElement(ex);
            }
            list.setModel(defaultListModel);
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        String selectedItem = (String) list.getSelectedValue();
                        opener.openProject(TmcSettingsManager.get().getProjectBasePath() + File.separator + list.getParent().getParent().getName() + File.separator + selectedItem);
                    }
                }
            };
            list.addMouseListener(mouseListener);
            panel.setName(course);
            panel.setViewportView(list);
            System.out.println(list.getParent().getParent().getName());
            tabbedPanelBase.addTab(course, panel);
        }
    }

    {
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        basePanel = new JPanel();
        basePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPanelBase = new JTabbedPane();
        basePanel.add(tabbedPanelBase, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        basePanel.add(toolbar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return basePanel;
    }
}
