package fi.helsinki.cs.tmc.intellij.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Shows a notification when an operation is in progress.
 */
public class OperationInProgressNotification {

    private static final Logger logger = LoggerFactory
            .getLogger(OperationInProgressNotification.class);
    private JFrame frame;

    public OperationInProgressNotification(String notification) {
        logger.info("Creating OperationInProgressNotification. @OperationInProgressNotification");
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
        logger.info("Hiding OperationInProgressNotification. @OperationInProgressNotification");
        this.frame.dispose();
        this.frame.setVisible(false);
    }
}
