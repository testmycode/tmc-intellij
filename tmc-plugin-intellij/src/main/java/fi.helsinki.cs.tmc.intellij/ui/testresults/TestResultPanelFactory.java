package fi.helsinki.cs.tmc.intellij.ui.testresults;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import fi.helsinki.cs.tmc.langs.domain.TestResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestResultPanelFactory implements ToolWindowFactory {

    private static List<TestResultsPanel> panels;

    public TestResultPanelFactory() {
        panels = new ArrayList<>();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        TestResultsPanel panel = new TestResultsPanel();
        ContentFactory cf = ContentFactory.SERVICE.getInstance();
        Content content = cf.createContent(panel.getBasePanel(), "", true);
        toolWindow.getContentManager().addContent(content);
        panels.add(panel);
    }

    public static void updateMostRecentResult(List<TestResult> result) {
        for (TestResultsPanel panel : panels) {
            panel.showTest(result);
            panel.getBasePanel().repaint();
        }
    }
}
