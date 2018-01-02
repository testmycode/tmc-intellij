package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;

import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/** Creates a tab in project list sidewindow and all components it requires. */
public class CourseTabFactory {

    private static final Logger logger = LoggerFactory.getLogger(CourseTabFactory.class);

    public void createCourseSpecificTab(
            ObjectFinder finder,
            ProjectOpener opener,
            String course,
            JTabbedPane tabbedPaneBase,
            CourseAndExerciseManager courseAndExerciseManager) {
        logger.info("Creating course specific tab. @CourseTabFactory");
        final JBScrollPane panel = new JBScrollPane();
        final JBList list = new JBList();
        list.setCellRenderer(new ProjectListRenderer());

        DefaultListModel defaultListModel = new DefaultListModel();
        panel.setBorder(BorderFactory.createTitledBorder(""));

        ProjectListManagerHolder.get()
                .addExercisesToList(finder, course, defaultListModel, courseAndExerciseManager);

        if (defaultListModel.getSize() <= 0) {
            return;
        }

        list.setName(course);
        list.setModel(defaultListModel);

        MouseListener mouseListener = createMouseListenerForWindow(opener, panel, list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(mouseListener);

        panel.setName(course);
        panel.setViewportView(list);

        ProjectListManagerHolder.get().addList(list);
        tabbedPaneBase.addTab(course, panel);
        tabbedPaneBase.addMouseListener(tabMouseListener(tabbedPaneBase));
        setScrollBarToBottom(course, tabbedPaneBase, panel);
    }

    @NotNull
    private MouseListener tabMouseListener(final JTabbedPane tabbedPanelBase) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() != 3) {
                    return;
                }

                PopUpMenu menu = new PopUpMenu();
                JBMenuItem openInExplorer = new JBMenuItem("Open path");

                openInExplorer.addActionListener(openCourseActionListener(tabbedPanelBase));

                JBMenuItem deleteFolder =
                        new JBMenuItem("Delete course "
                                + tabbedPanelBase.getSelectedComponent().getName());

                deleteFolder.addActionListener(deleteCourseActionListener(tabbedPanelBase));

                menu.add(openInExplorer);
                menu.add(deleteFolder);
                menu.show(tabbedPanelBase, mouseEvent.getX(), mouseEvent.getY());
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        };
    }

    @NotNull
    private ActionListener openCourseActionListener(final JTabbedPane tabbedPanelBase) {
        return actionEvent -> {
            try {
                logger.info("Opening course directory.");
                Desktop.getDesktop().open(new File(TmcSettingsManager
                        .get().getProjectBasePath()
                        + File.separator + tabbedPanelBase
                        .getSelectedComponent().getName()));
            } catch (IOException e) {
                logger.warn("Opening course directory failed.", e.getStackTrace());
                e.printStackTrace();
            }
        };
    }

    @NotNull
    private ActionListener deleteCourseActionListener(final JTabbedPane tabbedPanelBase) {
        return actionEvent -> {
            logger.info("Trying to delete course folder.");

            if (Messages
                    .showYesNoDialog("Are you sure you wish to permanently delete the course "
                                    + tabbedPanelBase.getSelectedComponent().getName()
                                    + " and all its exercises?",
                            "Delete Course", Messages.getWarningIcon()) != 0) {
                return;
            }

            try {
                FileUtils.deleteDirectory(new File(TmcSettingsManager
                        .get().getProjectBasePath()
                        + File.separator
                        + tabbedPanelBase.getSelectedComponent().getName()));
                new ProjectListWindow().refreshProjectList();
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.warn("Deleting course folder failed",
                        e1, e1.getStackTrace());
            }

        };
    }

    private void setScrollBarToBottom(String course,
                                      JTabbedPane tabbedPanelBase,
                                      JBScrollPane panel) {

        tabbedPanelBase.addTab(course, panel);
        JScrollBar bar = panel.getVerticalScrollBar();
        AdjustmentListener listener = event -> event.getAdjustable().setValue(event.getAdjustable().getMaximum());

        bar.addAdjustmentListener(listener);
        bar.setValueIsAdjusting(true);
        bar.removeAdjustmentListener(listener);
        bar.setValue(bar.getMaximum());
    }

    @NotNull
    private MouseListener createMouseListenerForWindow(
            final ProjectOpener opener, final JBScrollPane panel, final JBList list) {

        ObjectFinder finder = new ObjectFinder();

        logger.info("Creating mouse listener for course tab. @CourseTabFactory");
        return new MouseAdapter() {

            public void mousePressed(MouseEvent mouseEvent) {
                addRightMouseButtonFunctionality(mouseEvent, list, panel);
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() != 1 || mouseEvent.getClickCount() < 2) {
                    return;
                }

                Object selectedItem = list.getSelectedValue();
                if (selectedItem.getClass() == Exercise.class) {
                    logger.info("Getting TMC project directory "
                            + " from settingTmc. @CourseTabFactory");
                    opener.openProject(((Exercise) selectedItem)
                            .getExerciseDirectory(TmcSettingsManager.get()
                                    .getTmcProjectDirectory()));
                } else {
                    try {
                        String courseName = finder.findCourse(list.getParent().getParent().getName(), "title").getName();
                        opener.openProject(TmcSettingsManager.get().getProjectBasePath()
                                + File.separator + courseName
                                + File.separator + selectedItem, courseName);
                    } catch (NullPointerException e) {
                        new ErrorMessageService().showErrorMessage(e, "Course doesn't belong to the current organization", true);
                    }

                }

            }
        };
    }

    private void addRightMouseButtonFunctionality(MouseEvent mouseEvent,
                                                  final JBList list,
                                                  JBScrollPane panel) {

        logger.info("Adding functionality for right mouse button. @CourseTabFactory");
        if (!SwingUtilities.isRightMouseButton(mouseEvent)) {
            return;
        }

        int index = list.locationToIndex(mouseEvent.getPoint());
        list.setSelectedIndex(index);
        PopUpMenu menu = new PopUpMenu();
        JBMenuItem openInExplorer = new JBMenuItem("Open path");
        final Object selectedItem = list.getSelectedValue();
        JBMenuItem deleteFolder = new JBMenuItem("Delete folder");

        openInExplorer.addActionListener(createOpenInExploreListener(list, selectedItem));

        deleteFolder.addActionListener(createDeleteButtonActionListener(list, selectedItem));

        menu.add(openInExplorer);
        menu.add(deleteFolder);
        menu.show(panel, mouseEvent.getX(), mouseEvent.getY());
        menu.setLocation(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());

    }

    @NotNull
    private ActionListener createOpenInExploreListener(final JBList list,
                                                       final Object selectedItem) {
        return actionEvent -> {
            logger.info("Right mouse button action performed. @CourseTabFactory");

            try {
                if (selectedItem.getClass() != Exercise.class) {
                    Desktop.getDesktop().open(new File(TmcSettingsManager
                            .get().getProjectBasePath()
                            + File.separator + list.getParent()
                            .getParent().getName() + File.separator
                            + list.getSelectedValue()));
                } else {
                    logger.info("Getting TMC project directory "
                            + "from settingsTmc. @CourseTabFactory");
                    Desktop.getDesktop().open(new File(((Exercise)selectedItem)
                            .getExerciseDirectory(TmcSettingsManager
                                    .get().getTmcProjectDirectory()).toString()));
                }
            } catch (IOException e1) {
                logger.warn("IOException occurred. Something interrupted "
                                + "the mouse action. @CourseTabFactory",
                        e1, e1.getStackTrace());
                new ErrorMessageService().showErrorMessage(e1,
                        "IOException occurred. Something interrupted the mouse action.",
                        true);
            }
        };
    }

    @NotNull
    private ActionListener createDeleteButtonActionListener(final JBList list,
                                                            final Object selectedItem) {
        return actionEvent -> {
            logger.info("Trying to delete folder. @CourseTabFactory");
            if (Messages
                    .showYesNoDialog("Are you sure you wish to permanently delete this folder?",
                            "Delete Exercise", Messages.getWarningIcon()) != 0) {
                return;
            }

            try {
                if (selectedItem.getClass() != Exercise.class) {
                    FileUtils.deleteDirectory(new File(TmcSettingsManager
                            .get().getProjectBasePath()
                            + File.separator + list.getParent()
                            .getParent().getName() + File.separator
                            + list.getSelectedValue()));
                } else {
                    logger.info("Getting TMC project directory "
                            + "from settingsTmc. @CourseTabFactory");
                    FileUtils.deleteDirectory(new File(((Exercise) selectedItem)
                            .getExerciseDirectory(TmcSettingsManager
                                    .get().getTmcProjectDirectory()).toString()));
                }
                new ProjectListWindow().refreshProjectList();
            } catch (IOException e1) {
                logger.warn("IOException occurred. Something interrupted "
                                + "the mouse action. @CourseTabFactory",
                        e1, e1.getStackTrace());
                new ErrorMessageService().showErrorMessage(e1,
                        "IOException occurred. Something interrupted the mouse action.",
                        true);
            }
        };
    }
}
