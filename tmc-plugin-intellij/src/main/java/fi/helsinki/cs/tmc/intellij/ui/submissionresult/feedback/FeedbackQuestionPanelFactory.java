package fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback;

import fi.helsinki.cs.tmc.core.domain.submission.FeedbackQuestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackQuestionPanelFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(FeedbackQuestionPanelFactory.class);

    public static FeedbackQuestionPanel getPanelForQuestion(FeedbackQuestion question) {
        logger.info(
                "Checking if FeedbackQuestion {}" + " is int range question or text question.",
                question);
        if (question.isIntRange()) {
            return new IntRangeQuestionPanel(question);
        } else if (question.isText()) {
            return new TextQuestionPanel(question);
        }

        throw new IllegalArgumentException("Unknown feedback question type: " + question.getKind());
    }
}
