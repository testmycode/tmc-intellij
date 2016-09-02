package fi.helsinki.cs.tmc.intellij.ui.exercisedownloadlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class DownloadListWindow {

    private static final Logger logger = LoggerFactory.getLogger(DownloadListWindow.class);

    private JFrame frame;

    public void showDownloadableExercises(List<Exercise> exercises) {
        logger.info("Creating window to show downloadable exercises. @DownloadListWindow");
        frame = new JFrame();
        JPanel panel = new DownloadListPanel(exercises, this).getPanel();
        frame.add(panel);
        frame.setTitle("Available exercises");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    public void close() {
        logger.info("Closing downloadable exercises window @DownloadListWindow");
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
