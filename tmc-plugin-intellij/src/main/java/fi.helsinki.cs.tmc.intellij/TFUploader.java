package fi.helsinki.cs.tmc.intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import fi.helsinki.cs.tmc.intellij.services.SaveSettingsService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TFUploader implements ToolWindowFactory {

    private JButton buttonAction;
    private ToolWindow myToolWindow;

    final SaveSettingsService test = ServiceManager.getService(SaveSettingsService.class);

    public TFUploader() {

//         I assume it should print the saved string but it doesn't
        System.out.println(test.getuserName());
        System.out.println(test.getuserName());



    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    }
}