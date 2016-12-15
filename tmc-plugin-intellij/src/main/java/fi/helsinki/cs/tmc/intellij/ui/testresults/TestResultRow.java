package fi.helsinki.cs.tmc.intellij.ui.testresults;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;

/**
 * Class for unit test and validation test result rows. Provides style and structure.
 */
public class TestResultRow extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(TestResultRow.class);
    private final GridBagConstraints constraints = new GridBagConstraints();
    private final Color borderColor;
    private final Color titleColor;
    private final Color backgroundColor;

    private TestResultRow(Color borderColor,
                          Color titleColor,
                          String title,
                          String message,
                          String details) {
        logger.info("Constructing result row. @TestResultRow");

        this.borderColor = borderColor;
        this.titleColor = titleColor;
        this.backgroundColor = JBColor.background().brighter();

        createStyle();
        createTitle(title);
        if (message != null) {
            createContents(message);
        }
        if (details != null) {
            createDetails(details);
        }
    }

    public static TestResultRow createSuccessfulTestRow(
            String title,
            String message,
            String details) {
        logger.info("Creating successful test result row. @TestResultRow");
        return new TestResultRow(
                TestResultColors.TEST_BORDER_SUCCESS,
                TestResultColors.TEST_TITLE_SUCCESS,
                "PASS: " + title,
                message,
                details);
    }

    public static TestResultRow createFailedTestRow(
            String title,
            String message,
            String details) {
        logger.info("Creating failed test result row. @TestResultRow");
        return new TestResultRow(
                TestResultColors.TEST_BORDER_FAIL,
                TestResultColors.TEST_TITLE_FAIL,
                "FAIL: " + title,
                message,
                details);
    }

    public static TestResultRow createValidationRow(
            String title,
            String message,
            String details) {
        logger.info("Creating validation result row. @TestResultRow");
        return new TestResultRow(
                TestResultColors.TEST_BORDER_VALIDATION,
                TestResultColors.TEST_TITLE_VALIDATION,
                title,
                message,
                details);
    }

    private void createStyle() {
        logger.info("Creating style. @TestResultRow");

        this.setLayout(new GridBagLayout());
        this.setBackground(backgroundColor);

        createConstraints();
        createBorder();
    }

    private void createConstraints() {
        logger.info("Creating constraints. @TestResultRow");
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
    }

    private void createBorder() {
        logger.info("Creating border. @TestResultRow");

        Border innerPadding = BorderFactory.createEmptyBorder(5, 10, 5, 5);
        Border leftColorBar = BorderFactory.createMatteBorder(0, 6, 0, 0, borderColor);
        Border compoundBorder = BorderFactory.createCompoundBorder(leftColorBar, innerPadding);
        this.setBorder(compoundBorder);
    }

    private void createTitle(String title) {
        logger.info("Creating title. @TestResultRow");

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(titleColor);
        makeFontBigger(titleLabel);
        this.add(titleLabel, constraints);
    }

    private void makeFontBigger(JLabel titleLabel) {
        Font current = titleLabel.getFont();
        Font biggerFont = current.deriveFont(current.getSize() + 4f);
        titleLabel.setFont(biggerFont);
    }

    private void createContents(String message) {
        logger.info("Creating body. @TestResultRow");

        JLabel label = new JLabel(message);
        this.add(label, constraints);
    }

    private void createDetails(String details) {
        logger.info("Creating detailed message. @TestResultRow");

        JButton showButton = new JButton("Details");
        showButton.setBackground(backgroundColor); // prevent default bg border around button
        this.add(showButton, constraints);
        showButton.addActionListener(ae -> showDetailsPopup(details));
    }

    private void showDetailsPopup(String content) {
        logger.info("Showing details popup. @TestResultRow");
        JTextPane pane = new JTextPane();
        pane.setText(content);
        pane.setEditable(false);
        JBPopupFactory.getInstance()
                .createComponentPopupBuilder(pane, new JLabel("Message"))
                .createPopup()
                .showCenteredInCurrentWindow(
                        new ObjectFinder().findCurrentProject());
    }
}
