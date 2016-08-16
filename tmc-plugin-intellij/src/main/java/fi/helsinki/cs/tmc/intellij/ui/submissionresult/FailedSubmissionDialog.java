package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.ui.testresults.TestResultPanelFactory;

import com.intellij.openapi.project.Project;

public class FailedSubmissionDialog {

    public FailedSubmissionDialog(SubmissionResult result, Project project) {
        String points = parsePoints(result);

        String failMessage = "All tests didn't pass on server!\n"
                + "Permanent points awarded: " + points;
        TestResultPanelFactory.updateMostRecentResult(result.getTestCases());
    }

    private String parsePoints(SubmissionResult result) {
        if (result.getPoints().size() == 0) {
            return "0";
        }

        return result.getPoints().toString();
    }
}
