package fi.helsinki.cs.tmc.intellij.ui.elements;
 
import com.intellij.openapi.components.ServiceManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.PersistentTmcSettings;
import org.apache.commons.httpclient.HttpException;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class SettingsPanel {
    private JPanel panel1;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField serverAddressField;
    private JComboBox listOfAvailableCourses;
    private JButton refreshButton;
    private JFormattedTextField projectPathField;
    private JCheckBox checkForNewOrCheckBox;
    private JCheckBox checkThatAllActiveCheckBox;
    private JCheckBox sendSnapshotsOfYourCheckBox;
    private JComboBox selectErrorLanguageField;
    private JButton browseButton;

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JTextField getServerAddressField() {
        return serverAddressField;
    }

    public JComboBox getListOfAvailableCourses() {
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

    public JComboBox getselectErrorLanguageField() {
        return selectErrorLanguageField;
    }

    public JPanel getPanel() {
        return this.panel1;
    }


    public SettingsPanel() {
        SettingsTmc SettingsTmc = TmcSettingsManager.get();
        usernameField.setText(SettingsTmc.getUsername());
        serverAddressField.setText(SettingsTmc.getServerAddress());
        passwordField.setText(SettingsTmc.getPassword());
        ActionListener browseListener = createActionListener();
        browseButton.addActionListener(browseListener);
        ActionListener refreshListener = createActionListenerRefresh();
        refreshButton.addActionListener(refreshListener);
        ArrayList<Course> courses = new ArrayList<>();
        try {
            courses = (ArrayList<Course>) TmcCoreHolder.get().listCourses(ProgressObserver.NULL_OBSERVER).call();
        } catch (Exception e) {
            courses.add(new Course("vituiksmeni"));
        }
        for (Course crs : courses) {
            listOfAvailableCourses.addItem(crs);
            System.out.println(crs.getId());
            System.out.println(crs.getDetailsUrl());
            System.out.println(crs.getName());
            System.out.println(crs.isExercisesLoaded());
            System.out.println(crs.getCometUrl());
            System.out.println(crs.getExercises());
        }
        projectPathField.setText(SettingsTmc.getProjectBasePath());
        selectErrorLanguageField.addItem("English");
    }

    private ActionListener createActionListenerRefresh() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ArrayList<Course> courses = new ArrayList<>();
                listOfAvailableCourses.removeAllItems();
                try {
                    courses = (ArrayList<Course>) TmcCoreHolder.get().listCourses(ProgressObserver.NULL_OBSERVER).call();
                } catch (Exception e) {
                    courses.add(new Course("vituiksmeni"));
                }
                for (Course crs : courses) {
                    crs.setDetailsUrl(URI.create(crs.getDetailsUrl() + "?api_version=7"));
                    listOfAvailableCourses.addItem(crs);
                    System.out.println(crs.getId());
                    System.out.println(crs.getDetailsUrl());
                    System.out.println(crs.getName());
                    System.out.println(crs.isExercisesLoaded());
                    System.out.println(crs.getCometUrl());
                    System.out.println(crs.getExercises());
                    System.out.println(crs.getUnlockables());
                }
            }
        };
    }

    @NotNull
    private ActionListener createActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select path for projects");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showOpenDialog(panel1);
                projectPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        };
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
        panel1.setLayout(new GridLayoutManager(13, 3, new Insets(0, 0, 0, 0), -1, -1));
        usernameField = new JTextField();
        panel1.add(usernameField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        passwordField = new JPasswordField();
        panel1.add(passwordField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        serverAddressField = new JTextField();
        panel1.add(serverAddressField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        listOfAvailableCourses = new JComboBox();
        panel1.add(listOfAvailableCourses, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        refreshButton = new JButton();
        refreshButton.setText("Refresh");
        panel1.add(refreshButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        projectPathField = new JFormattedTextField();
        projectPathField.setEditable(false);
        projectPathField.setEnabled(false);
        panel1.add(projectPathField, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel1.add(separator2, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkForNewOrCheckBox = new JCheckBox();
        checkForNewOrCheckBox.setText("Check for new or updated exercises regularly");
        panel1.add(checkForNewOrCheckBox, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkThatAllActiveCheckBox = new JCheckBox();
        checkThatAllActiveCheckBox.setText("Check that all active exercises are open on startup");
        panel1.add(checkThatAllActiveCheckBox, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendSnapshotsOfYourCheckBox = new JCheckBox();
        sendSnapshotsOfYourCheckBox.setText("Send snapshots of your progress for study");
        panel1.add(sendSnapshotsOfYourCheckBox, new GridConstraints(9, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel1.add(separator3, new GridConstraints(10, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectErrorLanguageField = new JComboBox();
        panel1.add(selectErrorLanguageField, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Error message language");
        panel1.add(label1, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Current course");
        panel1.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Server address");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Password");
        panel1.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Username");
        panel1.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Folder for projects");
        panel1.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        browseButton = new JButton();
        browseButton.setText("Browse");
        panel1.add(browseButton, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
