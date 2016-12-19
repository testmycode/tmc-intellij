package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.actions.buttonactions.UploadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;
import fi.helsinki.cs.tmc.langs.domain.RunResult;
import fi.helsinki.cs.tmc.langs.domain.TestResult;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunningService {

    private static final Logger logger = LoggerFactory.getLogger(TestRunningService.class);

    public void runTests(
            final Exercise exercise,
            Project project,
            ThreadingService threadingService,
            ObjectFinder finder) {
        logger.info("Starting to run tests for current project. @TestRunningService");

        ProgressWindow window =
                ProgressWindowMaker.make("Running tests", project, true, true, true);
        CoreProgressObserver observer = new CoreProgressObserver(window);

        if (exercise != null) {
            prepareThreadForRunningTests(
                    exercise, project, threadingService, finder, window, observer);
        } else {
            Exception exception = new Exception();
            logger.warn(
                    "Running tests failed, exercise {} was not "
                            + "recognized. @TestRunningService",
                    exercise,
                    exception);
            new ErrorMessageService()
                    .showMessage(
                            exception, "Running tests failed, exercise was not recognized", true);
        }
    }

    private void prepareThreadForRunningTests(
            final Exercise exercise,
            final Project project,
            ThreadingService threadingService,
            ObjectFinder finder,
            ProgressWindow window,
            final CoreProgressObserver observer) {
        logger.info("Preparing thread for running tests. @TestRunningService");
        threadingService.runWithNotification(
                new Runnable() {
                    @Override
                    public void run() {
                        RunResult result = null;
                        try {
                            result = TmcCoreHolder.get().runTests(observer, exercise).call();
                            RunResult finalResult = result;
                            showTestResult(finalResult);
                            checkIfAllTestsPassed(finalResult, project);
                        } catch (Exception exception) {
                            logger.warn("Could not run tests. @TestRunningService", exception);
                            new ErrorMessageService()
                                    .showMessage(exception, "Running tests failed!", true);
                        }
                    }
                },
                project,
                window);
        displayTestWindow(finder);
    }

    private void checkIfAllTestsPassed(RunResult finalResult, Project project) {
        ApplicationManager.getApplication()
                .invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (allPassed(finalResult)) {
                                    if (Messages.showYesNoDialog(
                                                    project,
                                                    "Would you like to submit the exercise?",
                                                    "All tests passed!",
                                                    null)
                                            == 0) {
                                        new UploadExerciseAction().uploadExercise(project);
                                    }
                                }
                            }
                        });
    }

    private boolean allPassed(RunResult finalResult) {
        logger.info("Checking if all tests passed. @TestRunningService");
        for (TestResult test : finalResult.testResults) {
            if (!test.isSuccessful()) {
                return false;
            }
        }
        return true;
    }

    public void displayTestWindow(ObjectFinder finder) {
        logger.info("Displaying test window. @TestRunningService");
        Project project = finder.findCurrentProject();

        ToolWindowManager.getInstance(project).getToolWindow("TMC Test Results").show(null);
        ToolWindowManager.getInstance(project).getToolWindow("TMC Test Results").activate(null);
    }

    public void showTestResult(final RunResult finalResult) {
        logger.info("Showing the final test result after updating. @TestRunningService");
        ApplicationManager.getApplication()
                .invokeLater(
                        (new Runnable() {
                            @Override
                            public void run() {
                                TestResultPanelFactory.updateMostRecentResult(
                                        finalResult.testResults, null);
                            }
                        }));
    }
}
