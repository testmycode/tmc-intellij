package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.intellij.holders.ExerciseDatabaseManager;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.PropertySetter;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;


import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The actions to be executed on project startup
 * defined in plugin.xml exercises group on line
 * &lt;postStartupActivity implementation
 *   ="fi.helsinki.cs.tmc.intellij.actions.StartupEvent"&gt;
 */
public class StartupEvent implements StartupActivity {
    private static final Logger logger = LoggerFactory.getLogger(StartupEvent.class);

    @Override
    public void runActivity(@NotNull Project project) {
        logger.info("Opening project.");
        final OperationInProgressNotification note =
                new OperationInProgressNotification("Running TMC startup actions");

        PropertySetter propSet = new PropertySetter();
        propSet.setLog4jProperties();

        ExerciseDatabaseManager.setup();
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
        logger.info("Printing out project related information.");
        System.out.println(CourseAndExerciseManager.getDatabase());
        Long end = System.currentTimeMillis() - startTime;

        System.out.println("Time it took to download exercise information: " + end);
    }
}

