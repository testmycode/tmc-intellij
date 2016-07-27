package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import icons.TmcIcons;

import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Tells JList how to show its elements, allowing icons next
 * to exercises.
 */
public class ProjectListRenderer extends DefaultListCellRenderer {

    private Font font;

    public ProjectListRenderer() {
        if (TmcSettingsManager.get().getProjectBasePath().contains("/")) {
            font = new Font("ubuntu", Font.TRUETYPE_FONT, 13);
        } else {
            font = new Font("Arial", Font.TRUETYPE_FONT, 12);
        }
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        try {
            if (Exercise.class == value.getClass()) {
                if (((Exercise) value).isCompleted()) {
                    label.setIcon(TmcIcons.DONE_EXERCISE);
                } else {
                    label.setIcon(TmcIcons.NOT_DONE_EXERCISE);
                }
            } else {
                label.setIcon(TmcIcons.UNKNOWN);
            }
        } catch (Exception ewr) {

        }
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setFont(font);
        return label;
    }
}
