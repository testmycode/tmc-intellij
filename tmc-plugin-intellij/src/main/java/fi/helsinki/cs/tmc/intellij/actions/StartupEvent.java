package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.CheckForNewExercises;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.PropertySetter;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.spyware.ActivateSpywareListeners;

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
        logger.info("Opening project {} and running startup actions. @StartupEvent",
                project);

        ThreadingService threadingService = new ThreadingService();
        threadingService.runWithNotification(
                new Thread() {
                    @Override
                    public void run() {
                        PropertySetter propSet = new PropertySetter();
                        propSet.setLog4jProperties();

                        TmcSettingsManager.setup();
                        TmcCoreHolder.setup();

                        new ActivateSpywareListeners(project);
                        new CourseAndExerciseManager().initiateDatabase();

                        final EditorActionManager actionManager = EditorActionManager.getInstance();
                        final TypedAction typedAction = actionManager.getTypedAction();
                        TypedActionHandler originalHandler = actionManager.getTypedAction().getHandler();
                        typedAction.setupHandler(new ActivateSpywareAction(originalHandler));
                        new CheckForNewExercises().doCheck();
                    }
                },
                "Running TMC startup actions.",
                project);

    }
}

