package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;

import com.intellij.openapi.project.Project;

public class SubmissionResultHandler {

    public void showResultMessage(Exercise exercise,
                                         SubmissionResult result,
                                         Project project) {

        if (result.isAllTestsPassed()) {
            new SuccessfulSubmissionDialog(exercise, result, project);
        } else {
            new FailedSubmissionDialog(result, project);
        }
    }
}
