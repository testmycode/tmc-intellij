package fi.helsinki.cs.tmc.intellij.ui.settings;


import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.actions.buttonactions.DownloadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/**
 * Swing component displayed in settings window.
 */
public class SettingsPanel {

    private static final Logger logger = LoggerFactory.getLogger(SettingsPanel.class);
    private JPanel panel1;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField serverAddressField;
    private JComboBox<Course> listOfAvailableCourses;
    private JButton refreshButton;
    private JFormattedTextField projectPathField;
    private JCheckBox checkForNewOrCheckBox;
    private JCheckBox checkThatAllActiveCheckBox;
    private JCheckBox sendSnapshotsOfYourCheckBox;
    private JComboBox<String> selectErrorLanguageField;
    private JButton browseButton;
    private JButton okButton;
    private JButton cancelButton;
    private JButton downloadCourseExercisesButton;

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JTextField getServerAddressField() {
        return serverAddressField;
    }

    public JComboBox<Course> getListOfAvailableCourses() {
        return listOfAvailableCourses;
    }

    public JFormattedTextField getProjectPathField() {
        return projectPathField;
    }

    public JCheckBox getCheckForNewOrCheckBox() {
        return checkForNewOrCheckBox;
    }

    public JCheckBox getCheckThatAllActiveCheckBox() {
        return checkThatAllActiveCheckBox;
    }

    public JCheckBox getSendSnapshotsOfYourCheckBox() {
        return sendSnapshotsOfYourCheckBox;
    }

    public JComboBox<String> getselectErrorLanguageField() {
        return selectErrorLanguageField;
    }

    public JPanel getPanel() {
        return this.panel1;
    }

    public SettingsPanel(final JFrame frame) {
        logger.info("Building SettingsPanel");
        SettingsTmc settingsTmc = TmcSettingsManager.get();

        usernameField.setText(settingsTmc.getUsername());
        serverAddressField.setText(settingsTmc.getServerAddress());
        passwordField.setText(settingsTmc.getPassword());

        ActionListener browseListener = createActionListener();
        browseButton.addActionListener(browseListener);

        ActionListener refreshListener = createActionListenerRefresh();
        refreshButton.addActionListener(refreshListener);

        if (TmcSettingsManager.get().isSpyware()) {
            sendSnapshotsOfYourCheckBox.doClick();
        }
        if (TmcSettingsManager.get().isCheckForExercises()) {
            checkForNewOrCheckBox.doClick();
        }
        List<Course> courses = new ArrayList<>();

        try {
            courses =
                    TmcCoreHolder.get().listCourses(ProgressObserver.NULL_OBSERVER).call();
            logger.info("Getting list of courses from TmcCore. @SettingsPanel");
        } catch (Exception ignored) {
            logger.warn("Could not list Courses from TmcCore. @SettingsPanel",
                    ignored, ignored.getStackTrace());
            ignored.printStackTrace();
        }
        for (Course crs : courses) {
            listOfAvailableCourses.addItem(crs);
        }
        if (listOfAvailableCourses.getItemCount() == 0) {
            listOfAvailableCourses.addItem(TmcSettingsManager.get().getCourse());
        }

        listOfAvailableCourses.setSelectedItem(settingsTmc.getCourse());
        projectPathField.setText(settingsTmc.getProjectBasePath());
        selectErrorLanguageField.addItem("English");

        ActionListener okListener = createActionListenerOk(frame);
        okButton.addActionListener(okListener);
        ActionListener cancelListener = createActionListenerCancel(frame);
        cancelButton.addActionListener(cancelListener);

        ActionListener downloadListener = createActionListenerDownload(frame);
        downloadCourseExercisesButton.addActionListener(downloadListener);
    }

    private ActionListener createActionListenerDownload(final JFrame frame) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.info("Download button pressed. @SettingsPanel");

                saveInformation();

                Project project = new ObjectFinder().findCurrentProject();
                DownloadExerciseAction action = new DownloadExerciseAction();
                action.downloadExercises(project, false);

                frame.dispose();
                frame.setVisible(false);
            }
        };
    }

    public void saveInformation() {
        logger.info("Saving settings information. @SettingsPanel");
        final PersistentTmcSettings saveSettings =
                ServiceManager.getService(PersistentTmcSettings.class);
        SettingsTmc settingsTmc = ServiceManager.getService(PersistentTmcSettings.class)
                .getSettingsTmc();
        settingsTmc.setUsername(usernameField.getText());
        settingsTmc.setPassword(passwordField.getText());
        settingsTmc.setServerAddress(serverAddressField.getText());
        if (listOfAvailableCourses.getSelectedItem() != null) {
            Course course = (Course) listOfAvailableCourses.getSelectedItem();
            settingsTmc.setCourse(new ObjectFinder()
                    .findCourseByName(((Course) listOfAvailableCourses
                            .getSelectedItem()).getName(), TmcCoreHolder.get()));
            if (settingsTmc.getCourse() == null) {
                settingsTmc.setCourse(course);
            }
        }
        settingsTmc.setCheckForExercises(checkForNewOrCheckBox.isSelected());
        settingsTmc.setProjectBasePath(projectPathField.getText());
        settingsTmc.setSpyware(sendSnapshotsOfYourCheckBox.isSelected());
        saveSettings.setSettingsTmc(settingsTmc);
    }

    private ActionListener createActionListenerOk(final JFrame frame) {
        logger.info("Create action listener for SettingsPanel ok button. @SettingsPanel");
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new ButtonInputListener().receiveSettings();
                logger.info("Ok button pressed. @SettingsPanel");
                saveInformation();
                frame.dispose();
                frame.setVisible(false);
            }
        };
    }

    private ActionListener createActionListenerCancel(final JFrame frame) {
        logger.info("Create action listener for SettingsPanel cancel button. @SettingsPanel");
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.info("Cancel button pressed. @SettingsPanel");
                frame.dispose();
                frame.setVisible(false);
            }
        };
    }

    private ActionListener createActionListenerRefresh() {
        logger.info("Create action listener for SettingsPanel refresh button. @SettingsPanel");
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.info("Refresh button pressed. @SettingsPanel");
                List<Course> courses = new ArrayList<>();
                listOfAvailableCourses.removeAllItems();
                saveInformation();
                try {
                    logger.info("Getting list of courses from TmcCore. @SettingsPanel");
                    courses = (ArrayList<Course>)
                            TmcCoreHolder.get().listCourses(ProgressObserver.NULL_OBSERVER).call();
                } catch (Exception exception) {
                    logger.warn("Could not list Courses from TmcCore. @SettingsPanel",
                            exception, exception.getStackTrace());
                    ErrorMessageService error = new ErrorMessageService();
                    error.showMessage((TmcCoreException) exception, true);
                }

                addCourSesToListOfAvailable(courses);
                if ((TmcSettingsManager.get().getCourse()) != null) {
                    listOfAvailableCourses.setSelectedItem(TmcSettingsManager.get().getCourse());
                } else {
                    listOfAvailableCourses.setSelectedItem(getFirstFromAvailableCourses());
                }
                if (listOfAvailableCourses.getItemCount() == 0) {
                    listOfAvailableCourses.addItem(TmcSettingsManager.get().getCourse());
                }
            }
        };
    }

    private Course getFirstFromAvailableCourses() {
        return listOfAvailableCourses.getModel().getElementAt(0);
    }

    private void addCourSesToListOfAvailable(List<Course> courses) {
        logger.info("Adding courses to list of availabe courses. @SettingsPanel");
        for (Course crs : courses) {
            listOfAvailableCourses.addItem(crs);
        }
    }

    @NotNull
    private ActionListener createActionListener() {
        logger.info("Creating action listener for browsing. @SettingsPanel");
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.info("Browsing action performed. @SettingsPanel", actionEvent);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select path for projects");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showOpenDialog(panel1);

                if (fileChooser.getSelectedFile() == null) {
                    return;
                }
                projectPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        };
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        setupUi();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer.
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void setupUi() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(15, 6, new Insets(0, 0, 0, 0), -1, -1));
        usernameField = new JTextField();
        panel1.add(usernameField, new GridConstraints(1, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(12, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                GridConstraints.SIZEPOLICY_FIXED, null,
                new Dimension(11, 23), null, 0, false));
        passwordField = new JPasswordField();
        panel1.add(passwordField, new GridConstraints(2, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        serverAddressField = new JTextField();
        panel1.add(serverAddressField, new GridConstraints(3, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        listOfAvailableCourses = new JComboBox();
        panel1.add(listOfAvailableCourses, new GridConstraints(4, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        refreshButton = new JButton();
        refreshButton.setText("Refresh");
        panel1.add(refreshButton, new GridConstraints(4, 3, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(5, 0, 1, 6,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        projectPathField = new JFormattedTextField();
        projectPathField.setEditable(false);
        projectPathField.setEnabled(false);
        panel1.add(projectPathField, new GridConstraints(6, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel1.add(separator2, new GridConstraints(7, 0, 1, 6, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkForNewOrCheckBox = new JCheckBox();
        checkForNewOrCheckBox.setText("Check for new or updated exercises regularly");
        panel1.add(checkForNewOrCheckBox, new GridConstraints(8, 1, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendSnapshotsOfYourCheckBox = new JCheckBox();
        sendSnapshotsOfYourCheckBox.setText("Send snapshots of your progress for study");
        panel1.add(sendSnapshotsOfYourCheckBox, new GridConstraints(9, 1, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel1.add(separator3, new GridConstraints(10, 0, 1, 6,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        selectErrorLanguageField = new JComboBox();
        panel1.add(selectErrorLanguageField, new GridConstraints(11, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Error message language");
        panel1.add(label1, new GridConstraints(11, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Current course");
        panel1.add(label2, new GridConstraints(4, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Server address");
        panel1.add(label3, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Password");
        panel1.add(label4, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Username");
        panel1.add(label5, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Folder for projects");
        panel1.add(label6, new GridConstraints(6, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        browseButton = new JButton();
        browseButton.setText("Browse");
        panel1.add(browseButton, new GridConstraints(6, 3, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20),
                new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel1.add(cancelButton, new GridConstraints(13, 4, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("Ok");
        panel1.add(okButton, new GridConstraints(13, 3, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(20, -1),
                new Dimension(20, -1), new Dimension(20, -1), 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(14, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10),
                new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(4, 5, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(20, -1),
                new Dimension(20, -1), new Dimension(20, -1), 0, false));
        downloadCourseExercisesButton = new JButton();
        downloadCourseExercisesButton.setText("Download course exercises");
        panel1.add(downloadCourseExercisesButton, new GridConstraints(13, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * .
     * @noinspection ALL
     */
    public JComponent getRootComponent() {
        return panel1;
    }
}