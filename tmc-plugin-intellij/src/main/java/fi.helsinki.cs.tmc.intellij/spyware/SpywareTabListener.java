package fi.helsinki.cs.tmc.intellij.spyware;

import com.intellij.conversion.ComponentManagerSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ex.ComponentManagerEx;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.MessageBusUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.MessageHandler;
import com.intellij.util.messages.impl.MessageBusImpl;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.holders.TmcSettingsHolder;
import fi.helsinki.cs.tmc.core.utilities.JsonMaker;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;
import org.apache.commons.logging.Log;
import org.apache.tools.ant.types.Path;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.ui.content.ContentManagerEvent.ContentOperation.add;
import static org.apache.tools.ant.types.resources.MultiRootFileSet.SetType.file;

public class SpywareTabListener {

    private Project project;

    public SpywareTabListener(Project project) {
        this.project = project;
        createAndAddListeners(project);
    }

    private void createAndAddListeners(Project project) {
//        MessageBusConnection bus = project.getMessageBus().connect();
//        bus.setDefaultHandler(new MessageHandler() {
//            @Override
//            public void handle(Method method, Object... objects) {
//                System.out.println(method);
//                List<FileEditorManagerEvent> events = castToCorrectType(objects);
//                if (method.toString().toLowerCase().contains("fileopened")) {
//                    System.out.println("fleopened");
//                } else if (method.toString().toLowerCase().contains("fileclosed")) {
//                    System.out.println("fileclosed");
//                } else if (method.toString().toLowerCase().contains("selectionchanged")) {
//                    System.out.println("selectionchanged");
//                }
//                for (Object object : objects) {
//                    FileEditorManagerEvent event = (FileEditorManagerEvent) object;
//
//                    System.out.println(event.getNewFile().getName());
//                }
//            }
//        });
//        bus.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER);
        FileEditorManager.getInstance(project).addFileEditorManagerListener(new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
                String data = JsonMaker.create()
                        .add("opened_window", virtualFile.getName())
                        .toString();
                Exercise exercise = getExercise();
                LoggableEvent event;
                if (exercise != null) {
                    event = new LoggableEvent(exercise, "window_opened", data.getBytes(Charset.forName("UTF-8")));
                } else {
                    event = new LoggableEvent("window_opened", data.getBytes(Charset.forName("UTF-8")));
                }
                addEventToBuffer(event);
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
                String data = JsonMaker.create()
                        .add("closed_window", virtualFile.getName())
                        .toString();
                Exercise exercise = getExercise();
                LoggableEvent event;
                if (exercise != null) {
                    event = new LoggableEvent(exercise, "window_closed", data.getBytes(Charset.forName("UTF-8")));
                } else {
                    event = new LoggableEvent("window_closed", data.getBytes(Charset.forName("UTF-8")));
                }
                addEventToBuffer(event);
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
                String data = JsonMaker.create()
                        .add("new_value", fileEditorManagerEvent.getNewFile().getName())
                        .add("old_value", fileEditorManagerEvent.getOldFile().getName())
                        .add("file", fileEditorManagerEvent.getNewFile().getPath())
                        .toString();

                Exercise exercise = getExercise();
                LoggableEvent event;
                if (exercise != null) {
                    event = new LoggableEvent(exercise, "window_changed", data.getBytes(Charset.forName("UTF-8")));
                } else {
                    event = new LoggableEvent("window_changed", data.getBytes(Charset.forName("UTF-8")));
                }
                addEventToBuffer(event);
            }
        });
    }

    private void addEventToBuffer(LoggableEvent event) {
        if (TmcSettingsManager.get().isSpyware()
                && CourseAndExerciseManager.isCourseInDatabase(PathResolver
                .getCourseName(project.getBasePath()))) {
            SpywareEventManager.add(event);
            System.out.println(event);
        }
    }

    public Course getCourse() {
        SettingsTmc settings = TmcSettingsManager.get();
        if (settings.getCourse().getName().equals(PathResolver.getCourseName(project.getBasePath()))) {
            return settings.getCourse();
        }
        return new ObjectFinder().findCourseByName(PathResolver
                .getCourseName(project.getBasePath()), TmcCoreHolder.get());
    }

    public Exercise getExercise() {
        return PathResolver.getExercise(project.getBasePath());
    }
}
