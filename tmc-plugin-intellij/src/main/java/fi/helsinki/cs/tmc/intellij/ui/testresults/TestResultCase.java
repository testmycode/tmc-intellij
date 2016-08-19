package fi.helsinki.cs.tmc.intellij.ui.testresults;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.ui.popup.JBPopupFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory
            .getLogger(TestResultCase.class);
    private final GridBagConstraints constraints = new GridBagConstraints();
    private Color titleColor;
    private Color borderColor;

    public TestResultCase(final Color borderColor,
                          final Color titleColor,
                          final String title,
                          final String message,
                          final JPanel detailView,
                          final List<String> detailMessage) {

        logger.info("Creating items for TestResultsPanel. @TestResultCase");
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

        logger.info("Creating items for TestResultsPanel. @TestResultCase");
        createStyle(borderColor, titleColor);
        createTitle(title);
        createBody(message, detailView);
    }

    private void createDetailedMessage(List<String> detailMessage) {
        logger.info("Creating detailed message. @TestResultCase");
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
        logger.info("Creating body. @TestResultCase");
        createMessage(message);
        createDetailView(detailView);
    }

    private void createStyle(final Color borderColor, final Color titleColor) {
        logger.info("Creating style. @TestResultCase");
        this.borderColor = borderColor;
        this.titleColor = titleColor;

        this.setLayout(new GridBagLayout());

        createConstraints();
        createBorder();
    }

    private void createConstraints() {
        logger.info("Creating constraints. @TestResultCase");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
    }

    private void createTitle(final String title) {
        logger.info("Creating title. @TestResultCase");
        final JLabel titleLabel = new JLabel(title);
        this.add(titleLabel, constraints);
    }

    private void createTitle(final FileObject fileObject) {
        final JLabel titleLabel = new JLabel(fileObject.getName());
        this.add(titleLabel, constraints);
    }

    private void createMessage(final String message) {
        logger.info("Creating message. @TestResultCase");
        if (message != null) {
            this.add(new JLabel(message), constraints);
        }
    }

    private void createDetailView(final JPanel detailView) {
        logger.info("Creating detail view. @TestResultCase");
        if (detailView != null) {
            this.add(detailView, constraints);
        }
    }

    private void createBorder() {
        logger.info("Creating border for TestResultPanel. @TestResultCase");
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        final Border innerPadding = BorderFactory.createEmptyBorder(5, 10, 5, 5);
        final Border leftColorBar = BorderFactory.createMatteBorder(0, 6, 0, 0, borderColor);
        this.setBorder(BorderFactory.createCompoundBorder(leftColorBar, innerPadding));
    }
}
