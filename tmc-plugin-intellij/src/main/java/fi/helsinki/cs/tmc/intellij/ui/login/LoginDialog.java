package fi.helsinki.cs.tmc.intellij.ui.login;

import com.intellij.openapi.components.ServiceManager;
import fi.helsinki.cs.tmc.core.utilities.TmcServerAddressNormalizer;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import fi.helsinki.cs.tmc.intellij.ui.courseselection.CourseListWindow;
import fi.helsinki.cs.tmc.intellij.ui.organizationselection.OrganizationListWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel serverAddress;
    private JButton changeServerAddressButton;
    private SettingsTmc settingsTmc;

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        settingsTmc = ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();
        serverAddress.setText(settingsTmc.getServerAddress());

        this.setTitle("TMC Login");
        this.pack();

        buttonOK.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onOK();
                    }
                });

        buttonCancel.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onCancel();
                    }
                });

        changeServerAddressButton.addActionListener(createActionListenerChangeServerAddress());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        onCancel();
                    }
                });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onCancel();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void display() {
        logger.info("Showing Login window. @LoginDialog");

        LoginDialog dialog = new LoginDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        final PersistentTmcSettings saveSettings =
                ServiceManager.getService(PersistentTmcSettings.class);

        settingsTmc.setUsername(usernameField.getText());
        // settingsTmc.setServerAddress(serverAddress.getText());
        TmcServerAddressNormalizer.normalize();

        saveSettings.setSettingsTmc(settingsTmc);

        LoginManager loginManager = new LoginManager();

        if (loginManager.login(passwordField.getText())) {
            dispose();
            try {
                if (settingsTmc.getOrganization().isPresent()) {
                    CourseListWindow.display();
                } else {
                    OrganizationListWindow.display();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onCancel() {
        dispose();
    }

    private ActionListener createActionListenerChangeServerAddress() {
        return actionEvent -> {
            String newAddress =
                    JOptionPane.showInputDialog(
                            this, "Server address", this.serverAddress.getText());

            if (newAddress != null && !newAddress.trim().isEmpty()) {
                this.serverAddress.setText(newAddress.trim());
                // settingsTmc.setServerAddress(newAddress);
                TmcServerAddressNormalizer.normalize();

            }
        };
    }

    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
