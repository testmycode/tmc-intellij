package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;

import com.intellij.openapi.ui.Messages;

public class SubmissionResultHandler {

    public static void showResultMessage(Exercise exercise, SubmissionResult result, Project project) {
        if (result.isAllTestsPassed()) {
            new SuccessfulSubmissionDialog(exercise, result, project);

        } else {
            new FailedSubmissionDialog(result, project);
        }
    }
}
