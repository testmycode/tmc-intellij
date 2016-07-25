package fi.helsinki.cs.tmc.intellij.ui;

import icons.TmcIcons;

import javax.swing.*;
import java.awt.*;

public class ProjectListRenderer extends DefaultListCellRenderer {

    Font font = new Font("ubuntu", Font.TRUETYPE_FONT, 13);

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

//        try {
//            ObjectFinder finder = new ObjectFinder();
//            if (finder.findExerciseByName(finder.findCourseByName(list.getParent().getParent().getName(), TmcCoreHolder.get()), (String) value).isCompleted()){
//
//            };
//        } catch (Exception ewr) {
//
//        }
        label.setIcon(TmcIcons.SUBMIT_BUTTON);
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setFont(font);
        return label;
    }
}
