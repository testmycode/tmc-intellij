package fi.helsinki.cs.tmc.intellij.ui.elements;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import fi.helsinki.cs.tmc.intellij.actions.OpenToolWindowAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.ui.ProjectListRenderer;
import icons.TmcIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ProjectListWindow {

    private JTabbedPane tabbedPanelBase;

    public JPanel getBasePanel() {
        return basePanel;
    }

    private JPanel basePanel;
    private JButton openButton;
    private JButton hideButton;
    private JToolBar toolbar;

    public ProjectListWindow() {
        addCourseTabsAndExercises();
    }

    private void addCourseTabsAndExercises() {
        ObjectFinder finder = new ObjectFinder();
        ArrayList<String> courses = finder.listAllDownloadedCourses();
        final ProjectOpener opener = new ProjectOpener();
        for (String course : courses) {
            createCourseSpecificTab(finder, opener, course);
        }
        addFunctionalityToHideButton();
        JButton refreshButton = addFunctionalityToRefreshButton();
        toolbar.add(refreshButton);
        toolbar.add(new JButton("<-- REFRESH"));
        addFunctionalityToOpenButton();
    }

    private void addFunctionalityToOpenButton() {
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JBList list = (JBList) tabbedPanelBase
                        .getSelectedComponent().getComponentAt(10, 10)
                        .getComponentAt(10, 10);
                ProjectOpener opener = new ProjectOpener();
                String courseName =
                        (tabbedPanelBase.getSelectedComponent().getName());
                opener.openProject(TmcSettingsManager.get().getProjectBasePath()
                        + File.separator + courseName + File.separator
                        + list.getSelectedValue());
            }
        });
    }

    @NotNull
    private JButton addFunctionalityToRefreshButton() {
        JButton refreshButton = new JButton(TmcIcons.SUBMIT_BUTTON);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshProjectList();
            }
        });
        return refreshButton;
    }

    private void addFunctionalityToHideButton() {
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DataContext dataContext =
                        DataManager.getInstance().getDataContextFromFocus().getResult();
                Project project = DataKeys.PROJECT.getData(dataContext);
                new OpenToolWindowAction().hideToolWindow(project);
            }
        });
    }

    private void createCourseSpecificTab(ObjectFinder finder, ProjectOpener opener, String course) {
        final JBScrollPane panel = new JBScrollPane();
        final JBList list = new JBList();
        list.setCellRenderer(new ProjectListRenderer());
        DefaultListModel defaultListModel = new DefaultListModel();
        panel.setBorder(BorderFactory.createTitledBorder(""));
        ArrayList<String> exercises = finder.listAllDownloadedExercises(course);
        for (String ex : exercises) {
            defaultListModel.addElement(ex);
        }
        list.setModel(defaultListModel);
        MouseListener mouseListener = createMouseListenerForWindow(opener, panel, list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(mouseListener);
        panel.setName(course);
        panel.setViewportView(list);
        if (list.getItemsCount() > 0) {
            tabbedPanelBase.addTab(course, panel);
        }
    }

    @NotNull
    private MouseListener createMouseListenerForWindow(
            final ProjectOpener opener, final JBScrollPane panel, final JBList list) {
        return new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                addRightMouseButtonFunctionality(mouseEvent, list, panel);
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 1 && mouseEvent.getClickCount() >= 2) {
                    String selectedItem = (String) list.getSelectedValue();
                    opener.openProject(TmcSettingsManager.get().getProjectBasePath()
                            + File.separator + list.getParent().getParent().getName()
                            + File.separator + selectedItem);
                }
            }
        };
    }

    private void addRightMouseButtonFunctionality(
            MouseEvent mouseEvent, final JBList list, JBScrollPane panel) {
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            int index = list.locationToIndex(mouseEvent.getPoint());
            list.setSelectedIndex(index);
            PopUpMenu menu = new PopUpMenu();
            JBMenuItem openInExplorer = new JBMenuItem("Open path");
            openInExplorer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        Desktop.getDesktop().open
                                (new File(TmcSettingsManager.get().getProjectBasePath()
                                        + File.separator + list.getParent()
                                        .getParent().getName() + File.separator
                                        + list.getSelectedValue()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            menu.add(openInExplorer);
            menu.show(panel, mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public void refreshProjectList() {
        tabbedPanelBase.removeAll();
        toolbar.removeAll();
        addCourseTabsAndExercises();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        basePanel = new JPanel();
        basePanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPanelBase = new JTabbedPane();
        basePanel.add(tabbedPanelBase, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        toolbar = new JToolBar();
        toolbar.setBorderPainted(false);
        toolbar.setFloatable(false);
        basePanel.add(toolbar, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        basePanel.add(openButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hideButton = new JButton();
        hideButton.setText("Hide");
        basePanel.add(hideButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        basePanel.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return basePanel;
    }
}
