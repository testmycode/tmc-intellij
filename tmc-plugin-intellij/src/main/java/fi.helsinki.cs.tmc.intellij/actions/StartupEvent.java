package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;


import org.jetbrains.annotations.NotNull;

/**
 * The actions to be executed on project startup
 * defined in plugin.xml exercises group on line
 * &lt;postStartupActivity implementation
 *   ="fi.helsinki.cs.tmc.intellij.actions.StartupEvent"&gt;
 */
public class StartupEvent implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        final OperationInProgressNotification note =
                new OperationInProgressNotification("Running TMC startup actions");

        TmcSettingsManager.setup();
        TmcCoreHolder.setup();

        new OpenToolWindowAction().openToolWindow(project);

        Long start = System.currentTimeMillis();
        CourseAndExerciseManager.setup();

        printOutInfo(start);

        ProjectListManager.setup();
        note.hide();
    }

    private void printOutInfo(Long startTime) {
        System.out.println(CourseAndExerciseManager.getDatabase());
        Long end = System.currentTimeMillis() - startTime;

        System.out.println("Time it took to download exercise information: " + end);
    }
}

