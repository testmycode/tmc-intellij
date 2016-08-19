package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.actions.RunTestsAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;
import fi.helsinki.cs.tmc.langs.domain.RunResult;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.puppycrawl.tools.checkstyle.grammars.javadoc.JavadocLexer.exception;

public class TestRunningService {

    private static final Logger logger = LoggerFactory.getLogger(TestRunningService.class);

    public void runTests(final Exercise exercise, Project project,
                         ThreadingService threadingService,
                         ObjectFinder finder) {
        logger.info("Starting to run tests for current project. @TestRunningService");
        if (exercise != null) {
            threadingService.runWithNotification(new Runnable() {
                @Override
                public void run() {
                    RunResult result = null;
                    try {
                        result = TmcCoreHolder.get()
                                .runTests(ProgressObserver.NULL_OBSERVER, exercise).call();
                        RunResult finalResult = result;
                        showTestResult(finalResult);
                    } catch (Exception exception) {
                        logger.warn("Could not run tests. @TestRunningService", exception);
                        new ErrorMessageService().showMessage(exception,
                                "Running tests failed!", true);
                    }
                }
            }, "Running tests!", project);
            displayTestWindow(finder);
        } else {
            Exception exception = new Exception();
            logger.warn("Running tests failed, exercise {} was not "
                    + "recognized. @TestRunningService", exercise, exception);
            new ErrorMessageService().showMessage(exception,
                    "Running tests failed, exercise was not recognized",true);
        }
    }

    public void displayTestWindow(ObjectFinder finder) {
        logger.info("Displaying test window. @TestRunningService");
        Project project = finder.findCurrentProject();

        ToolWindowManager.getInstance(project)
                .getToolWindow("TMC Test Results").show(null);
        ToolWindowManager.getInstance(project)
                .getToolWindow("TMC Test Results").activate(null);
    }

    public void showTestResult(final RunResult finalResult) {
        logger.info("Showing the final test result after updating. @RunTestsAction");
        ApplicationManager.getApplication().invokeLater((new Runnable() {
            @Override
            public void run() {
                TestResultPanelFactory.updateMostRecentResult(finalResult.testResults);
            }
        }));
    }
}
