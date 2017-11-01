package fi.helsinki.cs.tmc.intellij.ui.login;

import com.intellij.openapi.components.ServiceManager;
import fi.helsinki.cs.tmc.core.exceptions.AuthenticationFailedException;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.login.LoginManager;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField serverAddressField;
    private SettingsTmc settingsTmc;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        settingsTmc = ServiceManager.getService(PersistentTmcSettings.class)
                .getSettingsTmc();
        serverAddressField.setText(settingsTmc.getServerAddress());

        this.setTitle("LOGIN PLS");
        this.pack();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void display() {
        LoginDialog dialog = new LoginDialog();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        final PersistentTmcSettings saveSettings =
                ServiceManager.getService(PersistentTmcSettings.class);

        settingsTmc.setUsername(usernameField.getText());
        settingsTmc.setPassword(passwordField.getText());
        settingsTmc.setServerAddress(serverAddressField.getText());

        saveSettings.setSettingsTmc(settingsTmc);

        LoginManager loginManager = new LoginManager();
        loginManager.login();

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
