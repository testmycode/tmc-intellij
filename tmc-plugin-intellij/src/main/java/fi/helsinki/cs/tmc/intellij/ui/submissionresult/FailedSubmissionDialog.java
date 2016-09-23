package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailedSubmissionDialog {

    private static final Logger logger = LoggerFactory.getLogger(FailedSubmissionDialog.class);

    public FailedSubmissionDialog(SubmissionResult result, Project project) {
        logger.info("Showing error message for failed submission. @FailedSubmissionDialog");
        String points = parsePoints(result);

        String failMessage =
                "All tests didn't pass on server!\nSee Test results for more details\n"
                        + "Permanent points awarded: "
                        + points;
        ApplicationManager.getApplication()
                .invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                Messages.showErrorDialog(failMessage, "Some tests failed!");
                            }
                        });
        TestResultPanelFactory.updateMostRecentResult(result.getTestCases());
    }

    private String parsePoints(SubmissionResult result) {
        logger.info(
                "Parsing which points are awarded from the failed submission."
                        + " @FailedSubmissionDialog");
        if (result.getPoints().size() == 0) {
            return "0";
        }

        return result.getPoints().toString();
    }
}
