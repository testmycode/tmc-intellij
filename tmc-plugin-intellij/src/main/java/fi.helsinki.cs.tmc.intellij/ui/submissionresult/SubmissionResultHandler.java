package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import fi.helsinki.cs.tmc.core.domain.Exercise;

import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.ui.SubmissionResultPopup;

import com.intellij.openapi.ui.Messages;

public class SubmissionResultHandler {

    public static void showResultMessage(Exercise exercise, SubmissionResult result) {
        if (result.isAllTestsPassed()) {
            //JTextField myTextField = new JTextField("Testin popupfactory", 20);
            new SuccessfulSubmissionDialog(exercise, result);

        } else {
            String errorMessage = "All tests didn't pass!";
            Messages.showErrorDialog(new SubmissionResultPopup().getPanel1(), errorMessage);
        }
    }
}
