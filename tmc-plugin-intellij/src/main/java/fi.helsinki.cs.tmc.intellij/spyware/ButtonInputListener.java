package fi.helsinki.cs.tmc.intellij.spyware;

import com.google.gson.Gson;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.spyware.LoggableEvent;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.Collections;

public class ButtonInputListener {

    public void receiveTestRun() {
        sendProjectActionEvent(getExercise(), "tmc.test");
    }

    public void receiveSubmit() {
        sendProjectActionEvent(getExercise(), "tmc.submit");
    }

    @Nullable
    private Exercise getExercise() {
        if (CourseAndExerciseManager.isCourseInDatabase(PathResolver
                .getCourseName(ObjectFinder.findCurrentProject().getBasePath()))) {
            return PathResolver.getExercise(ObjectFinder
                    .findCurrentProject().getBasePath());
        }
        return null;
    }

    public void receivePastebin() {
        sendProjectActionEvent(getExercise(), "tmc.paste");
    }

    public void receiveDownloadExercise() {
        sendProjectActionEvent(getExercise(), "tmc.download_exercise");
    }

    public void receiveSettings() {
        sendProjectActionEvent("tmc.settings_saved");
    }

    private void sendProjectActionEvent(String command) {
        Object data = Collections.singletonMap("command", command);
        String json = new Gson().toJson(data);
        byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
        LoggableEvent event = new LoggableEvent("ide_action", jsonBytes);
        addEvent(event);
    }

    private void addEvent(LoggableEvent event) {
        SpywareEventManager.add(event);
    }

    private void sendProjectActionEvent(Exercise ex, String command) {
        if (ex !=  null) {
            Object data = Collections.singletonMap("command", command);
            String json = new Gson().toJson(data);
            byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
            LoggableEvent event = new LoggableEvent(ex, "project_action", jsonBytes);
            addEvent(event);
        }
    }
}
