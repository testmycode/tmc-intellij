package fi.helsinki.cs.tmc.intellij.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class OperationInProgressNotification {

    private String notification;
    private JFrame frame;

    public OperationInProgressNotification(String notification) {
        frame = new JFrame();
        JPanel panel = new JPanel();
        panel.add(new JTextArea(notification));
        frame.setSize(new Dimension(500, 200));
        frame.add(panel);
        frame.setTitle(notification);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(new Dimension(500, 200));
    }

    public void hide() {
        this.frame.dispose();
        this.frame.setVisible(false);
    }
}
