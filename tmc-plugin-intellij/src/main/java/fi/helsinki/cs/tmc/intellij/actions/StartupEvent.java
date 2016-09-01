package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.ExerciseDatabaseManager;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.CheckForNewExercises;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ProgressWindowMaker;
import fi.helsinki.cs.tmc.intellij.services.PropertySetter;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.spyware.ActivateSpywareListeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.ToolWindowManager;

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

        ExerciseDatabaseManager.get();

        ThreadingService threadingService = new ThreadingService();

        ProgressWindow progressWindow = ProgressWindowMaker.make(
                "Running TMC startup actions.", project, false, false, false);

        CoreProgressObserver observer = new CoreProgressObserver(progressWindow);


        threadingService.runWithNotification(
                new Thread() {
                    @Override
                    public void run() {
                        observer.progress(0, 0.0, "Initializing loggers");
                        PropertySetter propSet = new PropertySetter();
                        propSet.setLog4jProperties();
                        observer.progress(0, 0.14, "Loading settings");
                        TmcSettingsManager.setup();
                        observer.progress(0, 0.28, "Holding core");
                        TmcCoreHolder.setup();
                        observer.progress(0, 0.42, "Activating listeners");
                        new ActivateSpywareListeners(project).activateListeners();
                        observer.progress(0, 0.56, "Initializing database");
                        new CourseAndExerciseManager().initiateDatabase();
                        observer.progress(0, 0.70, "Setting handlers");
                        final EditorActionManager actionManager = EditorActionManager.getInstance();
                        final TypedAction typedAction = actionManager.getTypedAction();
                        TypedActionHandler originalHandler = actionManager
                                .getTypedAction().getHandler();
                        typedAction.setupHandler(new ActivateSpywareAction(originalHandler));
                        observer.progress(0, 0.84, "Checking for new exercises");
                        if (TmcSettingsManager.get().isCheckForExercises()) {
                            new CheckForNewExercises().doCheck();
                        }
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                while (project.isDisposed()) {

                                }
                                if (ToolWindowManager.getInstance(project)
                                        .getToolWindow("Project") != null) {
                                    ToolWindowManager.getInstance(project)
                                            .getToolWindow("Project").activate(null);
                                }
                            }
                        });
                    }
                },
                project,
                progressWindow);
    }
}

