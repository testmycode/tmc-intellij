package fi.helsinki.cs.tmc.intellij.ui.submissionresult;

import static fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback.Boxer.hbox;
import static fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback.Boxer.hglue;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.submission.FeedbackAnswer;
import fi.helsinki.cs.tmc.core.domain.submission.FeedbackQuestion;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;

import fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback.FeedbackQuestionPanel;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback.FeedbackQuestionPanelFactory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class SuccessfulSubmissionDialog extends JDialog {



    private JButton okButton;

    private List<FeedbackQuestionPanel> feedbackQuestionPanels;

    public SuccessfulSubmissionDialog(Exercise exercise, SubmissionResult result, Project project) {
        this.setTitle(exercise.getName() + " passed");

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        setContentPane(contentPane);

        addYayLabel();
        addVSpace(6);
        if (exercise.requiresReview() && !result.getMissingReviewPoints().isEmpty()) {
            addRequiresReviewLabels();
        }
        addVSpace(6);
        addPointsLabel(result);
        addVSpace(10);
        addModelSolutionButton(result, project);
        addVSpace(20);
        addFeedbackQuestions(result); //TODO: maybe put in box
        addVSpace(10);
        addOkButton();

        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocusInWindow(true);
    }

    public void addOkListener(ActionListener okListener) {
        this.okButton.addActionListener(okListener);
    }

    public List<FeedbackAnswer> getFeedbackAnswers() {
        List<FeedbackAnswer> answers = new ArrayList<FeedbackAnswer>();
        for (FeedbackQuestionPanel panel : feedbackQuestionPanels) {
            FeedbackAnswer answer = panel.getAnswer();
            if (answer != null) {
                answers.add(answer);
            }
        }
        return answers;
    }

    private void addVSpace(int height) {
        add(Box.createVerticalStrut(height));
    }

    private Box leftAligned(Component component) {
        return hbox(component, hglue());
    }

    private void addYayLabel() {
        JLabel yayLabel = new JLabel("All tests passed on the server.");

        Font font = yayLabel.getFont();
        font = font.deriveFont(Font.BOLD, font.getSize2D() * 1.2f);
        yayLabel.setFont(font);

        yayLabel.setForeground(new java.awt.Color(0, 153, 51));
        //URL imageUrl = new URL("/fi/helsinki/cs/tmc/intellij/smile.gif");
        //ImageIcon icon = new ImageIcon(getClass().getResource("/smiley.gif"));
        //yayLabel.setIcon(icon);
        //new ImageIcon(getClass().getResource("/fi/helsinki/cs/tmc/smile.gif"));
        //yayLabel.setIcon(ConvenientDialogDisplayer.getDefault().getSmileyIcon());

        getContentPane().add(leftAligned(yayLabel));
    }

    private void addRequiresReviewLabels() {
        JLabel lbl1 = new JLabel("This exercise requires a code review.");
        String message = "It will have a yellow marker until it's accepted by an instructor.";
        JLabel lbl2 = new JLabel(message);
        getContentPane().add(leftAligned(lbl1));
        getContentPane().add(leftAligned(lbl2));
    }

    private void addPointsLabel(SubmissionResult result) {
        JLabel pointsLabel = new JLabel(getPointsMsg(result));
        pointsLabel.setFont(pointsLabel.getFont().deriveFont(Font.BOLD));

        getContentPane().add(leftAligned(pointsLabel));
    }

    private String getPointsMsg(SubmissionResult result) {
        if (!result.getPoints().isEmpty()) {
            String msg = "Points permanently awarded: "
                    + StringUtils.join(result.getPoints(), ", ") + ".";
            String str = StringEscapeUtils.escapeHtml4(msg).replace("\n", "<br />\n");
            return "<html>" + str + "</html>";
        } else {
            return "";
        }
    }


    private void addModelSolutionButton(SubmissionResult result, final Project project) {
        if (result.getSolutionUrl() != null) {
            final String solutionUrl = result.getSolutionUrl();
            JButton solutionButton = new JButton(new AbstractAction("View model solution") {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(new URI(solutionUrl));
                        } catch (Exception ex) {
                            String errorMessage = "Failed to open browser.\n" + ex.getMessage();
                            Messages.showErrorDialog(project, errorMessage, "Problem with browser");
                        }
                    } else {
                        String errorMessage = "Your OS doesn't support java.awt.Desktop.browser";
                        Messages.showErrorDialog(project, errorMessage, "Os problem");
                    }
                }
            });

            getContentPane().add(leftAligned(solutionButton));
        }
    }

    private void addFeedbackQuestions(SubmissionResult result) {
        this.feedbackQuestionPanels = new ArrayList<FeedbackQuestionPanel>();

        if (!result.getFeedbackQuestions().isEmpty() && result.getFeedbackAnswerUrl() != null) {
            for (FeedbackQuestion question : result.getFeedbackQuestions()) {
                try {
                    FeedbackQuestionPanel panel;
                    panel = FeedbackQuestionPanelFactory.getPanelForQuestion(question);
                    feedbackQuestionPanels.add(panel);
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }

            if (!feedbackQuestionPanels.isEmpty()) { // Some failsafety
                JLabel feedbackLabel = new JLabel("Feedback (leave empty to not send)");
                feedbackLabel.setFont(feedbackLabel.getFont().deriveFont(Font.BOLD));
                getContentPane().add(leftAligned(feedbackLabel));

                for (FeedbackQuestionPanel panel : feedbackQuestionPanels) {
                    getContentPane().add(leftAligned(panel));
                }
            } else {
                feedbackQuestionPanels = null;
            }
        }
    }

    private void addOkButton() {
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                setVisible(false);
                dispose();
            }
        });
        getContentPane().add(hbox(hglue(), okButton));
    }
}