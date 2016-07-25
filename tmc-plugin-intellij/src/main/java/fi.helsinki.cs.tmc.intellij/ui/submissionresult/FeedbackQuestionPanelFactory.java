package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import fi.helsinki.cs.tmc.core.domain.submission.FeedbackQuestion;

public class FeedbackQuestionPanelFactory {
    public static FeedbackQuestionPanel getPanelForQuestion(FeedbackQuestion question) {
        if (question.isIntRange()) {
            return new IntRangeQuestionPanel(question);
        } else if (question.isText()) {
            return new TextQuestionPanel(question);
        } else {
            throw new IllegalArgumentException(
                    "Unknown feedback question type: "
                    + question.getKind());
        }
    }
}
