package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListWindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import org.jetbrains.annotations.NotNull;

public class OpenToolWindowAction extends AnAction implements ToolWindowFactory {

    public void actionPerformed(AnActionEvent anActionEvent) {
        openToolWindow(anActionEvent.getProject());
    }

    public void openToolWindow(Project project) {
        try {
            ToolWindowManager.getInstance(project)
                    .getToolWindow("Project").setSplitMode(true, null);
            ToolWindowManager.getInstance(project)
                    .getToolWindow("Project").show(null);
            ToolWindowManager.getInstance(project)
                    .getToolWindow("TMC Project List").activate(null);
            ToolWindowManager.getInstance(project)
                    .getToolWindow("TMC Project List").setSplitMode(true, null);
            ToolWindowManager.getInstance(project)
                    .getToolWindow("TMC Project List").show(null);
        } catch (Exception exept) {

        }
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory cf = ContentFactory.SERVICE.getInstance();
        Content content = cf.createContent(new ProjectListWindow().getBasePanel(), "", true);
        toolWindow.getContentManager().addContent(content);
    }

    public void hideToolWindow(Project project) {
        ToolWindowManager.getInstance(project)
                .getToolWindow("TMC Project List").hide(null);
    }
}
