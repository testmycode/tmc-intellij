package fi.helsinki.cs.tmc.intellij.spyware;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;

import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Collections;

/**
 * When a button is pressed the responsible action tells this class that an action has happened.
 * This class then creates a LoggableEvent to be sent to the spyware server.
 * There are also checks in place to make sure that the course is a TMC course.
 */

public class ButtonInputListener {

    private static final Logger logger = LoggerFactory.getLogger(ButtonInputListener.class);

    public void receiveTestRun() {
        sendProjectActionEvent(getExercise(), "tmc.test");
    }

    public void receiveSubmit() {
        sendProjectActionEvent(getExercise(), "tmc.submit");
    }

    @Nullable
    private Exercise getExercise() {
        logger.info("Making sure current exercise should be tracked");
        if (new CourseAndExerciseManager().isCourseInDatabase(PathResolver
                .getCourseName(new ObjectFinder().findCurrentProject().getBasePath()))) {
            return PathResolver.getExercise(new ObjectFinder()
                    .findCurrentProject().getBasePath());
        }
        return null;
    }

    public void receiveRunAction() {
        sendProjectActionEvent(getExercise(), "COMMAND_RUN");
    }

    public void receiveDebugRunAction() {
        sendProjectActionEvent(getExercise(), "COMMAND_DEBUG");
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
        logger.info("Creating a project action event JSON.");
        Object data = Collections.singletonMap("command", command);
        String json = new Gson().toJson(data);
        byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
        LoggableEvent event = new LoggableEvent("ide_action", jsonBytes);
        addEvent(event);
    }

    private void sendProjectActionEvent(Exercise ex, String command) {
        if (ex == null) {
            logger.warn("Exercise was invalid.");
            return;
        }

        logger.info("Creating a project action event JSON.");
        Object data = Collections.singletonMap("command", command);
        String json = new Gson().toJson(data);

        byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
        LoggableEvent event = new LoggableEvent(ex, "project_action", jsonBytes);
        addEvent(event);
    }

    private void addEvent(LoggableEvent event) {
        logger.info("Checking that spyware setting is enabled.");
        SpywareEventManager.add(event);
    }
}
