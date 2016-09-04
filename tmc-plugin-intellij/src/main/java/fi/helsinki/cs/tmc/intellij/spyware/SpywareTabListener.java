package fi.helsinki.cs.tmc.intellij.spyware;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.utilities.JsonMaker;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class SpywareTabListener {

    private static final Logger logger = LoggerFactory.getLogger(SpywareTabListener.class);

    private Project project;
    private String basePath;

    public SpywareTabListener(Project project) {
        this.project = project;
        this.basePath = project.getBasePath();
        createAndAddListeners(project);
    }

    private void createAndAddListeners(Project project) {
        logger.info("Creating and adding listener to FileEditorManager.");
        FileEditorManager.getInstance(project)
                .addFileEditorManagerListener(new FileEditorManagerListener() {
                    @Override
                    public void fileOpened(@NotNull FileEditorManager fileEditorManager,
                                           @NotNull VirtualFile virtualFile) {
                        logger.info("Processing file opened event.");

                        String data = JsonMaker.create()
                                .add("opened_window", virtualFile.getName())
                                .toString();
                        Exercise exercise = getExercise();

                        LoggableEvent event;
                        if (exercise != null) {
                            event = new LoggableEvent(exercise,
                                    "window_opened", data.getBytes(Charset.forName("UTF-8")));
                        } else {
                            event = new LoggableEvent("window_opened",
                                    data.getBytes(Charset.forName("UTF-8")));
                        }
                        addEventToBuffer(event);
                    }

                    @Override
                    public void fileClosed(@NotNull FileEditorManager fileEditorManager,
                                           @NotNull VirtualFile virtualFile) {

                        logger.info("Processing fileClosed event.");
                        String data = JsonMaker.create()
                                .add("closed_window", virtualFile.getName())
                                .toString();

                        Exercise exercise = getExercise();
                        LoggableEvent event;

                        if (exercise != null) {
                            event = new LoggableEvent(exercise, "window_closed",
                                    data.getBytes(Charset.forName("UTF-8")));
                        } else {
                            event = new LoggableEvent("window_closed",
                                    data.getBytes(Charset.forName("UTF-8")));
                        }
                        addEventToBuffer(event);
                    }

                    @Override
                    public void selectionChanged(
                            @NotNull FileEditorManagerEvent fileEditorManagerEvent) {

                        logger.info("Processing selectionChanged event.");
                        String data;

                        if (fileEditorManagerEvent.getNewFile() == null) {
                            return;
                        }

                        if (fileEditorManagerEvent.getOldFile() != null) {
                            data = JsonMaker.create()
                                    .add("new_value",
                                            fileEditorManagerEvent.getNewFile().getName())
                                    .add("old_value",
                                            fileEditorManagerEvent.getOldFile().getName())
                                    .add("file", new PathResolver()
                                            .getPathRelativeToProject(fileEditorManagerEvent
                                                    .getNewFile().getPath()))
                                    .toString();
                        } else {
                            data = JsonMaker.create()
                                    .add("new_value", fileEditorManagerEvent
                                            .getNewFile().getName())
                                    .add("file", new PathResolver()
                                            .getPathRelativeToProject(fileEditorManagerEvent
                                                    .getNewFile().getPath()))
                                    .toString();
                        }

                        Exercise exercise = getExercise();
                        LoggableEvent event;
                        if (exercise != null) {
                            event = new LoggableEvent(exercise, "window_changed",
                                    data.getBytes(Charset.forName("UTF-8")));
                        } else {
                            event = new LoggableEvent("window_changed",
                                    data.getBytes(Charset.forName("UTF-8")));
                        }
                        addEventToBuffer(event);
                    }
                });
    }

    private void addEventToBuffer(LoggableEvent event) {
        if (!TmcSettingsManager.get().isSpyware() || new CourseAndExerciseManager()
                .isCourseInDatabase(PathResolver.getCourseName(basePath))) {
            return;
        }
        SpywareEventManager.add(event);
    }

    public Course getCourse() {
        SettingsTmc settings = TmcSettingsManager.get();
        if (settings.getCourse().getName().equals(PathResolver
                .getCourseName(basePath))) {
            return settings.getCourse();
        }
        return new ObjectFinder().findCourseByName(PathResolver
                .getCourseName(basePath), TmcCoreHolder.get());
    }

    public Exercise getExercise() {
        return PathResolver.getExercise(basePath);
    }
}
