package fi.helsinki.cs.tmc.intellij.ui.pastebin;

import fi.helsinki.cs.tmc.intellij.holders.ProjectListManagerHolder;
import fi.helsinki.cs.tmc.intellij.services.Exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.PasteService;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Creates the Pastebin submit panel.
 */
public class SubmitPanel {
    private JTextArea submitMessageTextArea;
    private JButton cancelButton;
    private JButton sendButton;
    private JLabel jlabel2;
    private JPanel jpanel1;

    public SubmitPanel(final PasteService pasteService) {
        setupUi();
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                pasteService.uploadToTmcPastebin(submitMessageTextArea.getText(),
                        new CourseAndExerciseManager(),
                        ProjectListManagerHolder.get());
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                pasteService.getWindow().close();
            }
        });
        jlabel2.setText("Creating pastebin item for exercise "
                + pasteService.getExerciseName());
    }

    public JPanel getPanel() {
        return jpanel1;
    }

    private void createUiComponents() {
        jlabel2 = new JLabel();
    }

    private void setupUi() {
        createUiComponents();
        jpanel1 = new JPanel();
        jpanel1.setLayout(new GridLayoutManager(3, 3,
                new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 1,
                new Insets(0, 0, 0, 0), -1, -1));
        jpanel1.add(panel1, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK
                        | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK
                        | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false));
        submitMessageTextArea = new JTextArea();
        panel1.add(submitMessageTextArea, new GridConstraints(4, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_WANT_GROW, null,
                new Dimension(150, 100), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Comment for paste (optional)");
        panel1.add(label1, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        jlabel2.setText("");
        panel1.add(jlabel2, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(15, -1),
                new Dimension(15, -1), new Dimension(15, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null,
                0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0),
                -1, -1));
        jpanel1.add(panel2, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK
                        | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK
                        | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel2.add(cancelButton, new GridConstraints(1, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK
                        | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null,
                null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null,
                null, 0, false));
        sendButton = new JButton();
        sendButton.setText("Send");
        panel2.add(sendButton, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK
                        | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null,
                null, 0, false));
        final Spacer spacer5 = new Spacer();
        jpanel1.add(spacer5, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10),
                new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final Spacer spacer6 = new Spacer();
        jpanel1.add(spacer6, new GridConstraints(1, 2, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(10, -1),
                new Dimension(10, -1), new Dimension(10, -1), 0, false));
        final Spacer spacer7 = new Spacer();
        jpanel1.add(spacer7, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_FIXED, 1, new Dimension(10, -1),
                new Dimension(10, -1), new Dimension(10, -1), 0, false));
    }

    public JComponent getRootComponent() {
        return jpanel1;
    }
}
