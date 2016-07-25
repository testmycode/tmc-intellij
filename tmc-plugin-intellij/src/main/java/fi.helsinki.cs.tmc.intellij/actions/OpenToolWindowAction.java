package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import fi.helsinki.cs.tmc.intellij.ui.elements.ProjectListWindow;
import org.jetbrains.annotations.NotNull;

public class OpenToolWindowAction extends AnAction implements ToolWindowFactory {


    public void actionPerformed(AnActionEvent anActionEvent) {

        ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow("TMC Project List").show(null);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory cf = ContentFactory.SERVICE.getInstance();
        Content content = cf.createContent(new ProjectListWindow().getBasePanel(), "", true);
        toolWindow.getContentManager().addContent(content);
    }
}
