package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.ToolWindowManager;

import org.jetbrains.annotations.NotNull;

public class StartupEvent implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        TmcSettingsManager.setup();
        TmcCoreHolder.setup();
        //Manager.getInstance(project).getToolWindow("TMC Project List").show(null);
    }
}

