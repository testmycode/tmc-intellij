package fi.helsinki.cs.tmc.intellij.ui.exercisedownloadlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.services.ExerciseDownloadingService;
import fi.helsinki.cs.tmc.intellij.ui.pastebin.SubmitPanel;

import javax.swing.*;
import java.util.List;

public class DownloadListWindow {

    private JFrame frame;

    public void showDownloadableExercises(List<Exercise> exercises){
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
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
