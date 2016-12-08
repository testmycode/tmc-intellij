package fi.helsinki.cs.tmc.intellij.ui.testresults;

import fi.helsinki.cs.tmc.langs.abstraction.ValidationResult;
import fi.helsinki.cs.tmc.langs.domain.TestResult;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestResultPanelFactory implements ToolWindowFactory {

    private static final Logger logger = LoggerFactory.getLogger(TestResultPanelFactory.class);
    private static List<TestResultsPanel> panels;

    public TestResultPanelFactory() {
        panels = new ArrayList<>();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        logger.info("Creating tool window content for test results. " + "@TestResultPanelFactory");

        TestResultsPanel panel = new TestResultsPanel();
        ContentFactory cf = ContentFactory.SERVICE.getInstance();
        Content content = cf.createContent(panel, "", true);
        toolWindow.getContentManager().addContent(content);

        panels.add(panel);
    }

    public static void updateMostRecentResult(List<TestResult> tests, ValidationResult validation) {
        logger.info("Updating the most recent test result. @TestResultPanelFactory.");
        for (TestResultsPanel panel : panels) {
            panel.showResults(tests, validation);
            panel.repaint();
        }
    }
}
