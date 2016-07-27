package fi.helsinki.cs.tmc.intellij.ui.projectlist;


import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import org.jetbrains.annotations.NotNull;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 * Creates a tab in project list sidewindow and all components it
 * requires.
 */

public class CourseTabFactory {

    public void createCourseSpecificTab(ObjectFinder finder,
                                        ProjectOpener opener, String course,
                                        JTabbedPane tabbedPanelBase) {
        final JBScrollPane panel = new JBScrollPane();
        final JBList list = new JBList();
        list.setCellRenderer(new ProjectListRenderer());
        DefaultListModel defaultListModel = new DefaultListModel();
        panel.setBorder(BorderFactory.createTitledBorder(""));
        ProjectListManager.addExercisesToList(finder, course, defaultListModel);
        if (defaultListModel.getSize() > 0) {
            list.setName(course);
            list.setModel(defaultListModel);
            MouseListener mouseListener = createMouseListenerForWindow(opener, panel, list);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addMouseListener(mouseListener);
            panel.setName(course);
            panel.setViewportView(list);
            ProjectListManager.addList(list);
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
                    Object selectedItem = list.getSelectedValue();
                    if (selectedItem.getClass() == Exercise.class) {
                        opener.openProject(((Exercise) selectedItem)
                                .getExerciseDirectory(TmcSettingsManager.get()
                                        .getTmcProjectDirectory()));
                    } else {
                        opener.openProject(TmcSettingsManager.get().getProjectBasePath()
                                + File.separator + list.getParent().getParent().getName()
                                + File.separator + selectedItem);
                    }
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
            final Object selectedItem = list.getSelectedValue();
            openInExplorer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        if (selectedItem.getClass() != Exercise.class) {
                            Desktop.getDesktop().open(new File(TmcSettingsManager
                                    .get().getProjectBasePath()
                                            + File.separator + list.getParent()
                                            .getParent().getName() + File.separator
                                            + list.getSelectedValue()));
                        } else {
                            Desktop.getDesktop().open(new File(((Exercise)selectedItem)
                                    .getExerciseDirectory(TmcSettingsManager
                                            .get().getTmcProjectDirectory()).toString()));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            menu.add(openInExplorer);
            menu.show(panel, mouseEvent.getX(), mouseEvent.getY());
        }
    }
}