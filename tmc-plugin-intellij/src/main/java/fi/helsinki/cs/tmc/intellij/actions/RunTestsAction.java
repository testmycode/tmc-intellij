package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;
import fi.helsinki.cs.tmc.langs.domain.RunResult;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTestsAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(RunTestsAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.info("Run tests action performed. @RunTestsAction");
        String[] cex = PathResolver.getCourseAndExerciseName(anActionEvent.getProject());
        runTests(CourseAndExerciseManager.get(cex[cex.length - 2],
                cex[cex.length - 1]), anActionEvent.getProject());
    }


    public void runTests(final Exercise exercise, Project project) {
        logger.info("Starting to run tests for current project. @RunTestsAction");
        if (exercise != null) {
            ThreadingService.runWithNotification(new Runnable() {
                @Override
                public void run() {
                    RunResult result = null;
                    try {
                        result = TmcCoreHolder.get()
                                .runTests(ProgressObserver.NULL_OBSERVER, exercise).call();
                        RunResult finalResult = result;
                        showTestResult(finalResult);
                    } catch (Exception exception) {
                        logger.warn("Could not run tests. @RunTestsAction", exception);
                        new ErrorMessageService().showMessage(exception,
                                "Running tests failed!", true);
                    }
                }
            }, "Running tests!", project);
            displayTestWindow();
        } else {
            Exception exception = new Exception();
            logger.warn("Running tests failed, exercise {} was not "
                    + "recognized. @RunTestsAction", exercise, exception);
            new ErrorMessageService().showMessage(exception,
                    "Running tests failed, exercise was not recognized",true);
        }
    }

    public static void displayTestWindow() {
        logger.info("Displaying test window. @RunTestsAction");
        ToolWindowManager.getInstance(new ObjectFinder().findCurrentProject())
                .getToolWindow("TMC Test Results").show(null);
        ToolWindowManager.getInstance(new ObjectFinder().findCurrentProject())
                .getToolWindow("TMC Test Results").activate(null);
    }

    public static void showTestResult(final RunResult finalResult) {
        logger.info("Showing the final test result after updating @RunTestsAction");
        ApplicationManager.getApplication().invokeLater((new Runnable() {
            @Override
            public void run() {
                TestResultPanelFactory.updateMostRecentResult(finalResult.testResults);
            }
        }));
    }
}
