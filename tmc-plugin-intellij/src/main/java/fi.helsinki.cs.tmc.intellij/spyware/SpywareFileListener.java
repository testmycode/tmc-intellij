package fi.helsinki.cs.tmc.intellij.spyware;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.utilities.JsonMaker;
import fi.helsinki.cs.tmc.spyware.EventReceiver;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mozilla.javascript.ScriptRuntime.add;

public class SpywareFileListener {

    private Project project;

    public SpywareFileListener(Project project) {
        createAndAddListener();
    }

    private void createAndAddListener() {
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void propertyChanged(@NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {
                String path = virtualFilePropertyEvent.getSource().toString().substring(7);
                if (Files.isDirectory(Paths.get(path))) {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "folder_rename")
                            .add("file", virtualFilePropertyEvent.getSource().toString())
                            .add("previous_name", virtualFilePropertyEvent.getOldValue().toString());
                    sendMetadata(metadata);
                } else {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "file_rename")
                            .add("file", virtualFilePropertyEvent.getSource().toString())
                            .add("previous_name", virtualFilePropertyEvent.getOldValue().toString());
                    sendMetadata(metadata);
                }
            }

            @Override
            public void contentsChanged(@NotNull VirtualFileEvent virtualFileEvent) {
                String path = virtualFileEvent.getSource().toString().substring(7);
                JsonMaker metadata = JsonMaker.create()
                        .add("cause", "file_change")
                        .add("file", virtualFileEvent.getSource().toString());
                sendMetadata(metadata);
            }

            @Override
            public void fileCreated(@NotNull VirtualFileEvent virtualFileEvent) {
                String path = virtualFileEvent.getSource().toString().substring(7);
                if (Files.isDirectory(Paths.get(path))) {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "folder_create")
                            .add("file", virtualFileEvent.getSource().toString());
                    sendMetadata(metadata);
                } else {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "file_create")
                            .add("file", virtualFileEvent.getSource().toString());
                    sendMetadata(metadata);
                }
            }

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent virtualFileEvent) {
            }

            @Override
            public void fileMoved(@NotNull VirtualFileMoveEvent virtualFileMoveEvent) {
                String path = virtualFileMoveEvent.getSource().toString().substring(7);
                if (Files.isDirectory(Paths.get(path))) {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "folder_move")
                            .add("file", path.toString())
                            .add("from", virtualFileMoveEvent.getOldParent().toString());
                    sendMetadata(metadata);
                } else {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "file_move")
                            .add("file", path)
                            .add("from", virtualFileMoveEvent.getOldParent().toString());
                    sendMetadata(metadata);
                }
            }

            @Override
            public void fileCopied(@NotNull VirtualFileCopyEvent virtualFileCopyEvent) {
                String path = virtualFileCopyEvent.getSource().toString().substring(7);
                if (Files.isDirectory(Paths.get(path))) {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "folder_copy")
                            .add("file", path)
                            .add("from", virtualFileCopyEvent.getOriginalFile().toString());
                    sendMetadata(metadata);
                } else {
                    JsonMaker metadata = JsonMaker.create()
                            .add("cause", "file_copy")
                            .add("file", path)
                            .add("from", virtualFileCopyEvent.getOriginalFile().toString());
                    sendMetadata(metadata);
                }
            }

            @Override
            public void beforePropertyChange(@NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {

            }

            @Override
            public void beforeContentsChange(@NotNull VirtualFileEvent virtualFileEvent) {

            }

            @Override
            public void beforeFileDeletion(@NotNull VirtualFileEvent virtualFileEvent) {
                String path = virtualFileEvent.getFile().getPath();
                JsonMaker metadata = JsonMaker.create()
                        .add("cause", "file_delete")
                        .add("file", path);
                sendMetadata(metadata);
            }

            @Override
            public void beforeFileMovement(@NotNull VirtualFileMoveEvent virtualFileMoveEvent) {

            }
        });
    }

    private void sendMetadata(JsonMaker metadata) {
        System.out.println(metadata);
    }

}
