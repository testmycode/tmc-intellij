package fi.helsinki.cs.tmc.intellij.ui.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Creates and controls the settings window.
 */
public class SettingsWindow {

    private static final Logger logger = LoggerFactory.getLogger(SettingsWindow.class);

    private JFrame frame;

    public SettingsWindow() {
        logger.info("Building SettingsWindow. @SettingsWindow");
        frame = new JFrame();
        JPanel panel = new SettingsPanel(frame).getPanel();

        frame.add(panel);
        frame.setTitle("TMC Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(new Dimension(800, 500));
    }

    public boolean isClosed() {
        return (frame == null || !frame.isVisible());
    }

    public void show() {
        logger.info("Showing SettingsWindow. @SettingsWindow");
        frame.setVisible(false);
        frame.setVisible(true);
    }
}