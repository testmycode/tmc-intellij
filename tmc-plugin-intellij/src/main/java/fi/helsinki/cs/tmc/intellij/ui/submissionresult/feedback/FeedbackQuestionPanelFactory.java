package fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback;

import fi.helsinki.cs.tmc.core.domain.submission.FeedbackQuestion;

public class FeedbackQuestionPanelFactory {
    public static FeedbackQuestionPanel getPanelForQuestion(FeedbackQuestion question) {
        if (question.isIntRange()) {
            return new IntRangeQuestionPanel(question);
        } else if (question.isText()) {
            return new TextQuestionPanel(question);
        }

        throw new IllegalArgumentException(
                    "Unknown feedback question type: "
                    + question.getKind());

    }
}
