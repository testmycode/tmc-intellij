package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import javax.swing.JPanel;

public abstract class FeedbackQuestionPanel extends JPanel {
    /**
     * Constructs an answer from the UI state, or null if no answer provided.
     */
    public abstract FeedbackAnswer getAnswer();
}
