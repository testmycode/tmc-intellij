package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.services.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;
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

/**
 * Defined in plugin.xml in actions group on line
 *  &lt;action id="Open TMC Exercise List"
 *  class="fi.helsinki.cs.tmc.intellij.actions.OpenToolWindowAction"&gt;
 *    and in extensions
 *   &lt;toolWindow id="TMC Project List" secondary="false" anchor="right"
 * factoryClass="fi.helsinki.cs.tmc.intellij.actions.OpenToolWindowAction"&gt;
 * <p>
 *   Opens the tool window in the active project window,
 *   generates the content for the window
 * </p>
 */
public class OpenToolWindowAction extends AnAction implements ToolWindowFactory {

    public void actionPerformed(AnActionEvent anActionEvent) {
        openToolWindow(anActionEvent.getProject());
    }

    public void openToolWindow(Project project) {
        try {
            ToolWindow projectList =  ToolWindowManager.getInstance(project)
                    .getToolWindow("TMC Project List");
            if (projectList.isVisible()) {
                projectList.hide(null);
            } else {
                ToolWindowManager.getInstance(project)
                        .getToolWindow("TMC Project List").show(null);
                ToolWindowManager.getInstance(project);
            }
        } catch (Exception exception) {
            ErrorMessageService service = new ErrorMessageService();
            service.showMessage(exception, "Opening TMC Project List Failed!");
        }
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ProjectListWindow window = new ProjectListWindow();
        ContentFactory cf = ContentFactory.SERVICE.getInstance();
        Content content = cf.createContent(window.getBasePanel(), "", true);
        toolWindow.getContentManager().addContent(content);
        ProjectListManager.addWindow(window);
    }

    public void hideToolWindow(Project project) {
        ToolWindowManager.getInstance(project)
                .getToolWindow("TMC Project List").hide(null);
    }
}
