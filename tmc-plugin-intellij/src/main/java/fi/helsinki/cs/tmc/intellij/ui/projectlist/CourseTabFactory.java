package fi.helsinki.cs.tmc.intellij.ui.projectlist;


import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
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

/**
 * Creates a tab in project list sidewindow and all components it
 * requires.
 */
public class CourseTabFactory {

    private static final Logger logger = LoggerFactory.getLogger(CourseTabFactory.class);

    public void createCourseSpecificTab(ObjectFinder finder,
                                        ProjectOpener opener, String course,
                                        JTabbedPane tabbedPanelBase) {
        logger.info("Creating course specific tab. @CourseTabFactory");
        final JBScrollPane panel = new JBScrollPane();
        final JBList list = new JBList();
        list.setCellRenderer(new ProjectListRenderer());

        DefaultListModel defaultListModel = new DefaultListModel();
        panel.setBorder(BorderFactory.createTitledBorder(""));
        ProjectListManager.addExercisesToList(finder, course, defaultListModel);

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

        ProjectListManager.addList(list);
        tabbedPanelBase.addTab(course, panel);
        setScrollBarToBottom(course, tabbedPanelBase, panel);
    }

    private void setScrollBarToBottom(String course, JTabbedPane tabbedPanelBase,
                                      JBScrollPane panel) {
        tabbedPanelBase.addTab(course, panel);
        JScrollBar bar = panel.getVerticalScrollBar();
        AdjustmentListener listener = new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent event) {
                event.getAdjustable().setValue(event.getAdjustable().getMaximum());
            }
        };
        bar.addAdjustmentListener(listener);
        bar.setValueIsAdjusting(true);
        bar.removeAdjustmentListener(listener);
        bar.setValue(bar.getMaximum());
    }

    @NotNull
    private MouseListener createMouseListenerForWindow(
            final ProjectOpener opener, final JBScrollPane panel, final JBList list) {
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
                if (selectedItem.getClass() == Exercise.class) {logger.info("Getting TMC project directory "
                         + " from settingTmc. @CourseTabFactory");
                    opener.openProject(((Exercise) selectedItem)
                            .getExerciseDirectory(TmcSettingsManager.get()
                                    .getTmcProjectDirectory()));
                } else {
                    opener.openProject(TmcSettingsManager.get().getProjectBasePath()
                            + File.separator + list.getParent().getParent().getName()
                            + File.separator + selectedItem);
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

        openInExplorer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
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
                    new ErrorMessageService().showMessage(e1,
                            "IOException occurred. Something interrupted the mouse action.",
                            true);
                }
            }
        });

        menu.add(openInExplorer);
        menu.show(panel, mouseEvent.getX(), mouseEvent.getY());

    }
}
