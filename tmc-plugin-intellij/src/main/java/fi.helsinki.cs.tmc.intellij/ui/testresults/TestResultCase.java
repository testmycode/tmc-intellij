package fi.helsinki.cs.tmc.intellij.ui.testresults;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.ui.popup.JBPopupFactory;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.tools.FileObject;

public final class TestResultCase extends JPanel {

    private final GridBagConstraints constraints = new GridBagConstraints();
    private Color titleColor;
    private Color borderColor;

    public TestResultCase(final Color borderColor,
                          final Color titleColor,
                          final String title,
                          final String message,
                          final JPanel detailView,
                          final List<String> detailMessage) {

        createStyle(borderColor, titleColor);
        createTitle(title);
        createBody(message, detailView);
        createDetailedMessage(detailMessage);
    }



    public TestResultCase(final Color borderColor,
                      final Color titleColor,
                      final String title,
                      final String message,
                      final JPanel detailView) {

        createStyle(borderColor, titleColor);
        createTitle(title);
        createBody(message, detailView);
    }

    private void createDetailedMessage(List<String> detailMessage) {
        if (detailMessage != null && detailMessage.size() > 0) {
            String detailedMessage = "";
            for (String line : detailMessage) {
                detailedMessage += line + "\n";
            }
            JButton button = new JButton("Details");
            this.add(button, constraints);
            final String finalDetailedMessage = detailedMessage;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JTextPane panel = new JTextPane();
                    panel.setText(finalDetailedMessage);
                    panel.setEditable(false);
                    JBPopupFactory.getInstance().createComponentPopupBuilder(panel,
                            new JLabel("Message")).createPopup().showCenteredInCurrentWindow(
                            new ObjectFinder().findCurrentProject());
                }
            });
        }
    }

    private void createBody(final String message, final JPanel detailView) {
        createMessage(message);
        createDetailView(detailView);
    }

    private void createStyle(final Color borderColor, final Color titleColor) {

        this.borderColor = borderColor;
        this.titleColor = titleColor;

        this.setLayout(new GridBagLayout());

        createConstraints();
        createBorder();
    }

    private void createConstraints() {
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
    }

    private void createTitle(final String title) {
        final JLabel titleLabel = new JLabel(title);
        this.add(titleLabel, constraints);
    }

    private void createTitle(final FileObject fileObject) {
        final JLabel titleLabel = new JLabel(fileObject.getName());
        this.add(titleLabel, constraints);
    }

    private void createMessage(final String message) {
        if (message != null) {
            this.add(new JLabel(message), constraints);
        }
    }

    private void createDetailView(final JPanel detailView) {
        if (detailView != null) {
            this.add(detailView, constraints);
        }
    }

    private void createBorder() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        final Border innerPadding = BorderFactory.createEmptyBorder(5, 10, 5, 5);
        final Border leftColorBar = BorderFactory.createMatteBorder(0, 6, 0, 0, borderColor);
        this.setBorder(BorderFactory.createCompoundBorder(leftColorBar, innerPadding));
    }
}
