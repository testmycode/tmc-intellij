package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import com.intellij.openapi.ui.Messages;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import com.intellij.openapi.project.Project;

public class FailedSubmissionDialog {

    public FailedSubmissionDialog(SubmissionResult result, Project project) {
        String failMessage = "All tests didn't pass on server!\n"
                + "Permanent points rewarded: " + result.getPoints().toString();
        Messages.showErrorDialog(project, failMessage, "All tests didn't pass on server!");
    }
}
