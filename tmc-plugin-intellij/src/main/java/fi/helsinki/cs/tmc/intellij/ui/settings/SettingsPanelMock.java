package fi.helsinki.cs.tmc.intellij.ui.settings;

import com.intellij.openapi.ui.ComboBox;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

import fi.helsinki.cs.tmc.intellij.ui.courseselection.CourseListWindow;
import fi.helsinki.cs.tmc.intellij.ui.organizationselection.OrganizationListWindow;


import java.awt.event.ActionListener;

import javax.swing.*;

/** Swing component displayed in settings window. */
public class SettingsPanelMock {

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
    private static SettingsPanelMock instance;
    private JFrame frame;

    public SettingsPanelMock(final JFrame frame) {
        this.frame = frame;
        this.instance = this;

        initComponents();

        SettingsTmc settingsTmc = new SettingsTmc("server-address", "kissa", "koira"); // TODO: HOW?

        loggedUser.setText("Logged in as " + settingsTmc.getUsername().get());
        setCurrentOrganization(null);
        setCurrentCourse(null);

        //        ActionListener browseListener = createActionListenerBrowse();
        //        browseButton.addActionListener(browseListener);
        changeOrganizationButton.addActionListener(createActionListenerChangeOrganization());
        changeCourseButton.addActionListener(createActionListenerChangeCourse());

        //        doClicks();

        projectPathField.setText(settingsTmc.getProjectBasePath());
        selectErrorLanguageField.addItem("English");

        ActionListener okListener = createActionListenerOk();
        okButton.addActionListener(okListener);
        ActionListener cancelListener = createActionListenerCancel();
        cancelButton.addActionListener(cancelListener);
        //        ActionListener downloadListener = createActionListenerDownload();
        //        downloadCourseExercisesButton.addActionListener(downloadListener);
        //        ActionListener logoutListener = createActionListenerLogout();
        //        logoutButton.addActionListener(logoutListener);
    }

    public void initComponents() {
        panel1 = new JPanel();
        projectPathField = new JFormattedTextField();
        checkForNewOrCheckBox = new JCheckBox();
        checkThatAllActiveCheckBox = new JCheckBox();
        sendDiagnosticsCheckBox = new JCheckBox();
        sendSnapshotsOfYourCheckBox = new JCheckBox();
        selectErrorLanguageField = new ComboBox<>();
        browseButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();
        downloadCourseExercisesButton = new JButton();
        logoutButton = new JButton();
        loggedUser = new JLabel();
        currentOrganization = new JLabel();
        changeOrganizationButton = new JButton();
        currentCourse = new JLabel();
        changeCourseButton = new JButton();
    }

    public static SettingsPanelMock getInstance() {
        return instance;
    }

    public JPanel getPanel() {
        //        doClicks();
        return this.panel1;
    }

    public JLabel getOrganizationLabel() {
        return currentOrganization;
    }

    public void setCurrentOrganization(Organization organization) {
        if (organization != null) {
            currentOrganization.setText(organization.getName());
        } else {
            currentOrganization.setText("No organization selected");
        }
    }

    public void setCurrentCourse(Course course) {
        if (course != null) {
            currentCourse.setText(course.getName());
        } else {
            currentCourse.setText("No course selected");
        }
    }

    //    public void doClicks() {
    //        if (TmcSettingsManager.get().isSpyware() != sendSnapshotsOfYourCheckBox.isSelected())
    // {
    //            sendSnapshotsOfYourCheckBox.doClick();
    //        }
    //        if (TmcSettingsManager.get().isCheckForExercises() !=
    // checkForNewOrCheckBox.isSelected()) {
    //            checkForNewOrCheckBox.doClick();
    //        }
    //        if (TmcSettingsManager.get().getSendDiagnostics() !=
    // sendDiagnosticsCheckBox.isSelected()) {
    //            sendDiagnosticsCheckBox.doClick();
    //        }
    //    }

    //    private ActionListener createActionListenerLogout() {
    //        return actionEvent -> {
    //            logger.info("Logout button pressed. @SettingsPanel");
    //
    //            LoginManager loginManager = new LoginManager();
    //            loginManager.logout();
    //
    //            this.frame.dispose();
    //            this.frame.setVisible(false);
    //            this.instance = null;
    //
    //            LoginDialog.display();
    //        };
    //    }
    //
    //    private ActionListener createActionListenerDownload() {
    //        return actionEvent -> {
    //            logger.info("Download button pressed. @SettingsPanel");
    //
    //            saveInformation();
    //
    //            Project project = new ObjectFinder().findCurrentProject();
    //            DownloadExerciseAction action = new DownloadExerciseAction();
    //            action.downloadExercises(project, false);
    //
    //            this.frame.dispose();
    //            this.frame.setVisible(false);
    //            this.instance = null;
    //        };
    //    }
    //
    //    public void saveInformation() {
    //        logger.info("Saving settings information. @SettingsPanel");
    //        final PersistentTmcSettings persistentSettings =
    //                ServiceManager.getService(PersistentTmcSettings.class);
    //        SettingsTmc settingsTmc =
    //                ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();
    //
    //        settingsTmc.setCheckForExercises(checkForNewOrCheckBox.isSelected());
    //        settingsTmc.setProjectBasePath(projectPathField.getText());
    //        settingsTmc.setSpyware(sendSnapshotsOfYourCheckBox.isSelected());
    //        settingsTmc.setSendDiagnostics(sendDiagnosticsCheckBox.isSelected());
    //        persistentSettings.setSettingsTmc(settingsTmc);
    //        if (sendDiagnosticsCheckBox.isSelected()) {
    //            try {
    //                TmcCoreHolder.get().sendDiagnostics(ProgressObserver.NULL_OBSERVER).call();
    //            } catch (Exception e) {
    //            }
    //        }
    //    }

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
        return actionEvent -> {
            new ButtonInputListener().receiveSettings();
            //            saveInformation();

            this.frame.dispose();
            this.frame.setVisible(false);
            this.instance = null;
        };
    }

    private ActionListener createActionListenerCancel() {
        return actionEvent -> {
            this.frame.dispose();
            this.frame.setVisible(false);
            this.instance = null;
        };
    }

    //    @NotNull
    //    private ActionListener createActionListenerBrowse() {
    //        logger.info("Creating action listener for browsing. @SettingsPanel");
    //        return actionEvent -> {
    //            logger.info("Browsing action performed. @SettingsPanel", actionEvent);
    //            JFileChooser fileChooser = new JFileChooser();
    //            fileChooser.setDialogTitle("Select path for projects");
    //            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //            fileChooser.showOpenDialog(panel1);
    //
    //            if (fileChooser.getSelectedFile() == null) {
    //                return;
    //            }
    //            projectPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    //        };
    //    }

    /**
     * .
     *
     * @noinspection ALL
     */
    public JComponent getRootComponent() {
        return panel1;
    }
}
