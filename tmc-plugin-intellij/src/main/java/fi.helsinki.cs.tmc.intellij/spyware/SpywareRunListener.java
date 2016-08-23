package fi.helsinki.cs.tmc.intellij.spyware;

import com.intellij.AppTopics;
import com.intellij.ProjectTopics;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx;
import com.intellij.openapi.vfs.impl.local.VirtualFileInfoAction;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerContainer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class SpywareRunListener  {

    private Project project;

    public SpywareRunListener(Project project) {
        this.project = project;
        connectToMessageBus(project);
    }

    private void connectToMessageBus(Project project) {
        MessageBusConnection bus = project.getMessageBus().connect();
        bus.setDefaultHandler(new MessageHandler() {
            @Override
            public void handle(Method method, Object... objects) {
                System.out.println(method);
                for (Object object : objects) {
                    if (method.toString().contains("contentSelected")) {
                        if (object.toString().toLowerCase().contains("debug")) {
                            new ButtonInputListener().receiveDebugRunAction();
                        } else if (object.toString().contains("DefaultRunExecutor")) {
                            new ButtonInputListener().receiveRunAction();
                        }
                    }
                }
            }
        });
//        bus.subscribe(AppTopics.FILE_DOCUMENT_SYNC);
//        bus.subscribe(ProjectTopics.MODULES);
        bus.subscribe(RunContentManager.TOPIC);
//        bus.subscribe(ProjectTopics.PROJECT_ROOTS);
    }
}
