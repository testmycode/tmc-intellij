package fi.helsinki.cs.tmc.intellij.ui.projectlist;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;

import icons.TmcIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ProjectListRenderer.class);

    private Font font;

    public ProjectListRenderer() {
        logger.info("Choosing font based on operating system. @ProjectListRenderer");
        if (osIsUnixBased()) {
            font = new Font("ubuntu", Font.TRUETYPE_FONT, 13);
        } else {
            font = new Font("Arial", Font.TRUETYPE_FONT, 12);
        }
    }

    private boolean osIsUnixBased() {
        return TmcSettingsManager.get().getProjectBasePath().contains("/");
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {


        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        try {
            if (exerciseUnKnown(value)) {
//                logger.info("Setting UNKNOWN TmcIcon "
//                        + "for exercise " + value + " in the project list window "
//                        + "@ProjectListManager");
                label.setIcon(TmcIcons.UNKNOWN);
            } else if (exerciseCompleted(value)) {
//                logger.info("Setting DONE_EXERCISE TmcIcon"
//                        + " for exercise " + value + " in the project list window "
//                        + "@ProjectListManager");
                label.setIcon(TmcIcons.DONE_EXERCISE);
            } else {
//                logger.info("Setting NOT_DONE_EXERCISE TmcIcon "
//                        + "for exercise " + value + " in the project list window "
//                        + "@ProjectListManager");
                label.setIcon(TmcIcons.NOT_DONE_EXERCISE);
            }
        } catch (Exception ewr) {
//            logger.info("Failed to set icon.", ewr, ewr.getStackTrace());
            new ErrorMessageService().showMessage(ewr, "Failed to set icon.", true);
        }

        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setFont(font);
        return label;
    }

    private boolean exerciseUnKnown(Object value) {
        return Exercise.class != value.getClass();
    }

    private boolean exerciseCompleted(Object value) {
        return ((Exercise) value).isCompleted();
    }
}
