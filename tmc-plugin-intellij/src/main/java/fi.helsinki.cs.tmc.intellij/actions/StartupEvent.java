package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.ExerciseDatabaseManager;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.PropertySetter;
import fi.helsinki.cs.tmc.intellij.ui.OperationInProgressNotification;
import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;

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

        CourseAndExerciseManager.setup();
        ProjectListManager.setup();
        final EditorActionManager actionManager = EditorActionManager.getInstance();
        final TypedAction typedAction = actionManager.getTypedAction();
        TypedActionHandler originalHandler = actionManager.getTypedAction().getHandler();
        typedAction.setupHandler(new ActivateSpywareAction(originalHandler));
        note.hide();
    }
}

