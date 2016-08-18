package fi.helsinki.cs.tmc.intellij.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;
import fi.helsinki.cs.tmc.langs.domain.RunResult;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

public class TestRunningService {

    public void runTests(final Exercise exercise, Project project,
                         ThreadingService threadingService,
                         ObjectFinder finder) {

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
                    } catch (Exception e) {
                        ErrorMessageService error = new ErrorMessageService();
                        error.showMessage(e, "Running tests failed!", true);
                    }
                }
            }, "Running tests!", project);
            displayTestWindow(finder);
        } else {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(new Exception(),
                    "Running tests failed, exercise was not recognized",true);
        }
    }

    public void displayTestWindow(ObjectFinder finder) {
        Project project = finder.findCurrentProject();

        ToolWindowManager.getInstance(project)
                .getToolWindow("TMC Test Results").show(null);
        ToolWindowManager.getInstance(project)
                .getToolWindow("TMC Test Results").activate(null);
    }

    public void showTestResult(final RunResult finalResult) {
        ApplicationManager.getApplication().invokeLater((new Runnable() {
            @Override
            public void run() {
                TestResultPanelFactory.updateMostRecentResult(finalResult.testResults);
            }
        }));
    }
}
