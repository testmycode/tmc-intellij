package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;

import fi.helsinki.cs.tmc.langs.domain.RunResult;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

public class RunTestsAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        String[] cex = PathResolver.getCourseAndExerciseName(anActionEvent.getProject());
        if (CourseAndExerciseManager.isCourseInDatabase(cex[cex.length - 2])) {
            runTests(CourseAndExerciseManager.get(cex[cex.length - 2],
                    cex[cex.length - 1]), anActionEvent.getProject());
        } else {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(new Exception(), "Running tests failed, exercise was not recognized");
        }
    }


    public void runTests(final Exercise exercise, Project project) {
        if (exercise != null) {
            new ButtonInputListener().receiveTestRun();
            ThreadingService.runWithNotification(new Runnable() {
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
                        error.showMessage(e, "Running tests failed!");
                    }
                }
            }, "Running tests!", project);
            displayTestWindow();
        } else {
            ErrorMessageService error = new ErrorMessageService();
            error.showMessage(new Exception(), "Running tests failed, exercise was not recognized");
        }
    }

    public static void displayTestWindow() {
        ToolWindowManager.getInstance(new ObjectFinder().findCurrentProject())
                .getToolWindow("TMC Test Results").show(null);
        ToolWindowManager.getInstance(new ObjectFinder().findCurrentProject())
                .getToolWindow("TMC Test Results").activate(null);
    }

    public static void showTestResult(final RunResult finalResult) {
        ApplicationManager.getApplication().invokeLater((new Runnable() {
            @Override
            public void run() {
                TestResultPanelFactory.updateMostRecentResult(finalResult.testResults);
            }
        }));
    }
}
