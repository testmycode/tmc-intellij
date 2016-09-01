package fi.helsinki.cs.tmc.intellij.ui.testresults;

import fi.helsinki.cs.tmc.langs.domain.TestResult;
import com.intellij.ui.JBProgressBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.List;



public class TestResultsPanel {

    private static final Logger logger = LoggerFactory
            .getLogger(TestResultsPanel.class);
    private JPanel basePanel;
    private JPanel newpanel;

    public TestResultsPanel() {
        addTestCases();
    }

    private void addTestCases() {
        logger.info("Adding Test cases. @TestResultsPanel");
        basePanel = new JPanel();
        basePanel.setLayout(new GridLayout(1,1));
        final JScrollPane scrollPane1 = new JBScrollPane();
        basePanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null, 0, false));
        newpanel = new JPanel();
        newpanel.setLayout(new GridLayout(12,1));
        scrollPane1.setViewportView(newpanel);
    }

    public JPanel getBasePanel() {
        return basePanel;
    }

    public void showTest(List<TestResult> results) {
        logger.info("Showing all test results. @TestResultsPanel");
        newpanel.removeAll();
        newpanel.setLayout(new GridLayout(results.size() + 1, 1));
        JBProgressBar bar = new TestResultProgressBar();
        //bar.setBorderPainted(false);
        //bar.setForeground(Color.green);
        //bar.setStringPainted(true);
        //bar.setBorder(BorderFactory.createLineBorder(Color.black));

        newpanel.add(bar);
        int success = 0;
        for (TestResult result : results) {
            List<String> error;
            if (result.getDetailedMessage().size() > 0) {
                error = result.getDetailedMessage();
            } else {
                error = result.getException();
            }
            newpanel.add(new TestResultCase(getColor(result.isSuccessful()),
                    Color.BLUE, result.getName(), result.getMessage(),
                    new JPanel(), error));
            if (result.isSuccessful()) {
                success++;
            }
        }
        bar.setMinimum(0);
        bar.setMaximum(100);
        bar.setStringPainted(true);
        bar.setValue((int) (100 * ((double) success / results.size())));
        basePanel.repaint();
    }

    private Color getColor(boolean successful) {
        if (successful) {
            return Color.GREEN;
        }
        return Color.RED;
    }
}
