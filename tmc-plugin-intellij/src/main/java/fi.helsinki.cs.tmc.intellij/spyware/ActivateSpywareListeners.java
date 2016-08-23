package fi.helsinki.cs.tmc.intellij.spyware;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.MessageHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import static com.intellij.ui.content.ContentManagerEvent.ContentOperation.add;

public class ActivateSpywareListeners {

    private Project project;

    public ActivateSpywareListeners(Project project) {
        this.project = project;
        new SpywareRunListener(project);
        new SpywareFileListener(project);
        new SpywareTabListener(project);
    }
}
