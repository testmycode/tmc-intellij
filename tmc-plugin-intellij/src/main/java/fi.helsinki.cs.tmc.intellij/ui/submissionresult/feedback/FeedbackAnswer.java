package fi.helsinki.cs.tmc.intellij.ui.submissionresult.feedback;

import fi.helsinki.cs.tmc.core.domain.submission.FeedbackQuestion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;




public class FeedbackAnswer {
    private FeedbackQuestion question;
    private String answer;

    public FeedbackAnswer(FeedbackQuestion question) {
        this(question, "");
    }

    public FeedbackAnswer(FeedbackQuestion question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public FeedbackQuestion getQuestion() {
        return question;
    }

    public void setQuestion(FeedbackQuestion question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("question_id", question.getId());
        obj.addProperty("answer", answer);
        return new Gson().toJson(obj);
    }
}
