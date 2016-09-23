package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.ui.exercisedownloadlist.CustomCheckBoxList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;

public class ExerciseCheckBoxService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseCheckBoxService.class);

    public static List<Exercise> filterDownloads(
            CustomCheckBoxList checkBoxes, List<Exercise> exercises) {
        logger.info("Filtering exercises. @ExerciseCheckBoxService");
        List<Exercise> downloadThese = new ArrayList<>();
        for (JCheckBox box : checkBoxes) {
            if (box.isSelected()) {
                for (Exercise ex : exercises) {
                    if (box.getText().equals(ex.getName())) {
                        downloadThese.add(ex);
                    }
                }
            }
        }
        return downloadThese;
    }

    public static void toggleAllCheckBoxes(CustomCheckBoxList exerciselist) {
        logger.info(
                "Toggling all exercise check boxes checked or unchecked. @ExerciseCheckBoxService");
        int checked = 0;
        int all = 0;
        for (JCheckBox box : exerciselist) {
            all++;
            if (box.isSelected()) {
                checked++;
            }
            box.setSelected(true);
        }
        if (all == checked) {
            for (JCheckBox box : exerciselist) {
                box.setSelected(false);
            }
        }
    }
}
