package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.actions.buttonactions.UploadExerciseAction;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;
import fi.helsinki.cs.tmc.langs.domain.RunResult;
import fi.helsinki.cs.tmc.langs.domain.SpecialLogs;
import fi.helsinki.cs.tmc.langs.domain.TestResult;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

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
                    .showErrorMessageWithExceptionDetails(
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
                () -> {
                    RunResult result;
                    try {
                        result = TmcCoreHolder.get().runTests(observer, exercise).call();
                    } catch (Exception exception) {
                        logger.warn("Could not run tests. @TestRunningService", exception);
                        new ErrorMessageService()
                                .showErrorMessageWithExceptionDetails(exception, "Running tests failed!", true);
                        return;
                    }

                    if (isErrorStatus(result.status)) {
                        String stdout = getLog(result, SpecialLogs.STDOUT);
                        String stderr = getLog(result, SpecialLogs.STDERR);

                        String message = (result.status == RunResult.Status.COMPILE_FAILED)
                                ? "Something went wrong while compiling the code. See details below."
                                : "Something went wrong while running the tests. See details below.";

                        new ErrorMessageService().showPopupWithDetails(
                                message,
                                "Test Error",
                                stderr + System.lineSeparator() + stdout,
                                NotificationType.ERROR
                        );
                        return;
                    }

                    showTestResult(result);
                    checkIfAllTestsPassed(result, project);
                },
                project,
                window);
        displayTestWindow(finder);
    }

    private String getLog(RunResult result, String log) {
        return new String(result.logs.get(log), StandardCharsets.UTF_8);
    }

    private boolean isErrorStatus(RunResult.Status status) {
        return status == RunResult.Status.COMPILE_FAILED
            || status == RunResult.Status.GENERIC_ERROR;
    }

    private void checkIfAllTestsPassed(RunResult finalResult, Project project) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            if (allPassed(finalResult)) {
                                if (Messages.showYesNoDialog(
                                                project,
                                                "Would you like to submit the exercise?",
                                        "All Tests Passed!",
                                                null)
                                        == 0) {
                                    new UploadExerciseAction().uploadExercise(project);
                                }
                            }
                        });
    }

    private boolean allPassed(RunResult finalResult) {
        logger.info("Checking if all tests passed. @TestRunningService");
        return finalResult.testResults.stream().allMatch(TestResult::isSuccessful);
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
                        (() -> TestResultPanelFactory.updateMostRecentResult(
                                finalResult.testResults, null)));
    }
}
