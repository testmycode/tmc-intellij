package fi.helsinki.cs.tmc.intellij.ui.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.actions.buttonactions.DownloadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.ProgressWindowMaker;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import fi.helsinki.cs.tmc.intellij.ui.courseselection.CourseListWindow;
import fi.helsinki.cs.tmc.intellij.ui.login.LoginDialog;
import fi.helsinki.cs.tmc.intellij.ui.organizationselection.OrganizationListWindow;
import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

/**
 * Swing component displayed in settings window.
 */
public class SettingsPanel {

    private static final Logger logger = LoggerFactory.getLogger(SettingsPanel.class);
    private JPanel panel1;
    private JFormattedTextField projectPathField;
    private JCheckBox checkForNewOrCheckBox;
    private JCheckBox checkThatAllActiveCheckBox;
    private JCheckBox sendDiagnosticsCheckBox;
    private JCheckBox sendSnapshotsOfYourCheckBox;
    private JComboBox<String> selectErrorLanguageField;
    private JButton browseButton;
    private JButton okButton;
    private JButton cancelButton;
    private JButton downloadCourseExercisesButton;
    private JButton logoutButton;
    private JLabel loggedUser;
    private JLabel currentOrganization;
    private JButton changeOrganizationButton;
    private JLabel currentCourse;
    private JButton changeCourseButton;
    private static SettingsPanel instance;
    private JFrame frame;
    private Organization organizationFirst;

    public SettingsPanel(final JFrame frame) {
        this.frame = frame;
        this.instance = this;

        logger.info("Building SettingsPanel");
        SettingsTmc settingsTmc = TmcSettingsManager.get();

        loggedUser.setText("Logged in as " + settingsTmc.getUsername().get());

        setCurrentOrganization();
        organizationFirst = settingsTmc.getOrganization().orNull();

        setCurrentCourse();

        ActionListener browseListener = createActionListenerBrowse();
        browseButton.addActionListener(browseListener);

        changeOrganizationButton.addActionListener(createActionListenerChangeOrganization());

        changeCourseButton.addActionListener(createActionListenerChangeCourse());

        doClicks();

        projectPathField.setText(settingsTmc.getProjectBasePath());
        selectErrorLanguageField.addItem("English");

        ActionListener okListener = createActionListenerOk();
        okButton.addActionListener(okListener);
        ActionListener cancelListener = createActionListenerCancel();
        cancelButton.addActionListener(cancelListener);
        ActionListener downloadListener = createActionListenerDownload();
        downloadCourseExercisesButton.addActionListener(downloadListener);
        ActionListener logoutListener = createActionListenerLogout();
        logoutButton.addActionListener(logoutListener);
    }

    public JPanel getPanel() {
        doClicks();
        return this.panel1;
    }

    public static SettingsPanel getInstance() {
        return instance;
    }

    public void setCurrentOrganization() {
        final PersistentTmcSettings persistentSettings =
                ServiceManager.getService(PersistentTmcSettings.class);
        SettingsTmc settings = persistentSettings.getSettingsTmc();

        if (settings.getOrganization().isPresent()
                && settings.getOrganization().get().getName() != null) {
            currentOrganization.setText(settings.getOrganization().get().getName());
        } else {
            currentOrganization.setText("No organization selected");
        }
    }

    public void setCurrentCourse() {
        final PersistentTmcSettings persistentSettings =
                ServiceManager.getService(PersistentTmcSettings.class);
        SettingsTmc settings = persistentSettings.getSettingsTmc();

        if (settings.getCurrentCourse().isPresent()
                && settings.getCurrentCourse().get().getTitle() != null) {
            currentCourse.setText(settings.getCourseName());
        } else {
            currentCourse.setText("No course selected");
        }
    }

    public void doClicks() {
        if (TmcSettingsManager.get().isSpyware() != sendSnapshotsOfYourCheckBox.isSelected()) {
            sendSnapshotsOfYourCheckBox.doClick();
        }
        if (TmcSettingsManager.get().isCheckForExercises() != checkForNewOrCheckBox.isSelected()) {
            checkForNewOrCheckBox.doClick();
        }
        if (TmcSettingsManager.get().getSendDiagnostics() != sendDiagnosticsCheckBox.isSelected()) {
            sendDiagnosticsCheckBox.doClick();
        }
    }

    private ActionListener createActionListenerLogout() {
        return actionEvent -> {
            logger.info("Logout button pressed. @SettingsPanel");

            LoginManager loginManager = new LoginManager();
            loginManager.logout();

            this.frame.dispose();
            this.frame.setVisible(false);
            this.instance = null;

            LoginDialog.display();
        };
    }

    private ActionListener createActionListenerDownload() {
        return actionEvent -> {
            logger.info("Download button pressed. @SettingsPanel");

            saveInformation();

            Project project = new ObjectFinder().findCurrentProject();
            DownloadExerciseAction action = new DownloadExerciseAction();
            action.downloadExercises(project, false);

            this.frame.dispose();
            this.frame.setVisible(false);
            this.instance = null;
        };
    }

    public void saveInformation() {
        logger.info("Saving settings information. @SettingsPanel");
        final PersistentTmcSettings persistentSettings =
                ServiceManager.getService(PersistentTmcSettings.class);
        SettingsTmc settingsTmc =
                ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();

        settingsTmc.setCheckForExercises(checkForNewOrCheckBox.isSelected());
        settingsTmc.setProjectBasePath(projectPathField.getText());
        settingsTmc.setSpyware(sendSnapshotsOfYourCheckBox.isSelected());
        settingsTmc.setSendDiagnostics(sendDiagnosticsCheckBox.isSelected());
        persistentSettings.setSettingsTmc(settingsTmc);
        if (sendDiagnosticsCheckBox.isSelected()) {
            try {
                TmcCoreHolder.get().sendDiagnostics(ProgressObserver.NULL_OBSERVER).call();
            } catch (Exception e) {
            }
        }
    }

    private ActionListener createActionListenerChangeOrganization() {
        return actionEvent -> {
            try {
                OrganizationListWindow.display();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private ActionListener createActionListenerChangeCourse() {
        return actionEvent -> {
            try {
                CourseListWindow.display();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private ActionListener createActionListenerOk() {
        logger.info("Create action listener for SettingsPanel ok button. @SettingsPanel");
        return actionEvent -> {
            new ButtonInputListener().receiveSettings();
            logger.info("Ok button pressed. @SettingsPanel");
            saveInformation();

            this.frame.dispose();
            this.frame.setVisible(false);
            this.instance = null;

            SettingsTmc settingsTmc =
                    ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();

            if (settingsTmc.getOrganization().orNull() != organizationFirst) {
                ApplicationManager.getApplication()
                        .invokeLater(
                                () -> {
//                                    new ErrorMessageService()
//                                            .showInfoBalloon(
//                                                    "Organization has been changed. Refreshing project list...");

                                    ProjectListManagerHolder.get().refreshAllCourses();

//                                    new ErrorMessageService()
//                                            .showInfoBalloon("Project list has been refreshed.");
                                });
            }
        };
    }

    private ActionListener createActionListenerCancel() {
        logger.info("Create action listener for SettingsPanel cancel button. @SettingsPanel");
        return actionEvent -> {
            logger.info("Cancel button pressed. @SettingsPanel");
            this.frame.dispose();
            this.frame.setVisible(false);
            this.instance = null;
        };
    }

    @NotNull
    private ActionListener createActionListenerBrowse() {
        logger.info("Creating action listener for browsing. @SettingsPanel");
        return actionEvent -> {
            logger.info("Browsing action performed. @SettingsPanel", actionEvent);
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setDialogTitle("Select path for projects");
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.showOpenDialog(panel1);

            File projectDefaultFolder = folderChooser.getSelectedFile();

            if (projectDefaultFolder == null) {
                return;
            }

            final String projectDefaultFolderPath = projectDefaultFolder.getAbsolutePath();

            if (projectDefaultFolderPath.toLowerCase().contains("onedrive")) {
                new ErrorMessageService().showErrorMessagePopup("OneDrive doesn't work with the TMC plugin for IntelliJ.\n" +
                    "Please use another folder for your projects.");
                return;
            }

            projectPathField.setText(projectDefaultFolderPath);
        };
    }

    /**
     * .
     *
     * @noinspection ALL
     */
    public JComponent getRootComponent() {
        return panel1;
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
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(14, 6, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(11, 23), null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(3, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectPathField = new JFormattedTextField();
        projectPathField.setEditable(false);
        projectPathField.setEnabled(false);
        panel1.add(projectPathField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel1.add(separator2, new GridConstraints(5, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkForNewOrCheckBox = new JCheckBox();
        checkForNewOrCheckBox.setText("Check for new or updated exercises regularly");
        panel1.add(checkForNewOrCheckBox, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendDiagnosticsCheckBox = new JCheckBox();
        sendDiagnosticsCheckBox.setSelected(true);
        sendDiagnosticsCheckBox.setText("Automatically send crash reports and diagnostics for plugin development");
        panel1.add(sendDiagnosticsCheckBox, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendSnapshotsOfYourCheckBox = new JCheckBox();
        sendSnapshotsOfYourCheckBox.setSelected(true);
        sendSnapshotsOfYourCheckBox.setText("Send snapshots of your progress for study");
        panel1.add(sendSnapshotsOfYourCheckBox, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel1.add(separator3, new GridConstraints(9, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectErrorLanguageField = new JComboBox();
        panel1.add(selectErrorLanguageField, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Error message language");
        panel1.add(label1, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Current course");
        panel1.add(label2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Organization");
        panel1.add(label3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Folder for projects");
        panel1.add(label4, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        browseButton = new JButton();
        browseButton.setText("Browse");
        panel1.add(browseButton, new GridConstraints(4, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel1.add(cancelButton, new GridConstraints(12, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("Ok");
        panel1.add(okButton, new GridConstraints(12, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(13, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(20, -1), new Dimension(20, -1), new Dimension(20, -1), 0, false));
        downloadCourseExercisesButton = new JButton();
        downloadCourseExercisesButton.setText("Download course exercises");
        panel1.add(downloadCourseExercisesButton, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoutButton = new JButton();
        logoutButton.setText("Log out");
        panel1.add(logoutButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loggedUser = new JLabel();
        loggedUser.setText("Label");
        panel1.add(loggedUser, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentOrganization = new JLabel();
        currentOrganization.setText("Label");
        panel1.add(currentOrganization, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeOrganizationButton = new JButton();
        changeOrganizationButton.setText("Change");
        panel1.add(changeOrganizationButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentCourse = new JLabel();
        currentCourse.setText("Label");
        panel1.add(currentCourse, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeCourseButton = new JButton();
        changeCourseButton.setText("Change");
        panel1.add(changeCourseButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
