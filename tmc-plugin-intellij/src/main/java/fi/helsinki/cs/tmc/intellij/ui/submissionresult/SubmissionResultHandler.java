package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;

import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionResultHandler {

    private static final Logger logger = LoggerFactory
            .getLogger(SubmissionResultHandler.class);

    public void showResultMessage(Exercise exercise,
                                         SubmissionResult result,
                                         Project project) {
        logger.info("Showing submission result message. @SubmissionResultHandler");

        if (result.isAllTestsPassed()) {
            new SuccessfulSubmissionDialog(exercise, result, project);
        } else {
            new FailedSubmissionDialog(result, project);
        }
    }
}
