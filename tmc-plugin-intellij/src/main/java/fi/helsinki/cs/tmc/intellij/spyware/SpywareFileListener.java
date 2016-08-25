package fi.helsinki.cs.tmc.intellij.spyware;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.utilities.JsonMaker;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.spyware.spywareutils.ActiveThreadSet;
import fi.helsinki.cs.tmc.intellij.spyware.spywareutils.RecursiveZipper;
import fi.helsinki.cs.tmc.spyware.LoggableEvent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SpywareFileListener implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(SpywareFileListener.class);
    private Project project;
    private ActiveThreadSet snapshotterThreads;
    private boolean closed;

    public SpywareFileListener(Project project) {
        this.project = project;
        this.snapshotterThreads = new ActiveThreadSet();
        createAndAddListener();
    }

    private void createAndAddListener() {
        VirtualFileManager.getInstance()
                .addVirtualFileListener(new VirtualFileListener() {
                    @Override
                    public void propertyChanged(
                            @NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {
                        logger.info("Processing propertyChanged event.");
                        if (isProperfile(virtualFilePropertyEvent.getFile())) {
                            String path = virtualFilePropertyEvent.getSource()
                                    .toString().substring(7);
                            if (Files.isDirectory(Paths.get(path))) {
                                JsonMaker metadata = JsonMaker.create()
                                        .add("cause", "folder_rename")
                                        .add("file", new PathResolver()
                                                .getPathRelativeToProject(path))
                                        .add("previous_name", virtualFilePropertyEvent
                                                .getOldValue().toString());
                                sendMetadata(metadata);
                            } else {
                                JsonMaker metadata = JsonMaker.create()
                                        .add("cause", "file_rename")
                                        .add("file", new PathResolver()
                                                .getPathRelativeToProject(path))
                                        .add("previous_name", virtualFilePropertyEvent
                                                .getOldValue().toString());
                                sendMetadata(metadata);
                            }
                        }
                    }

                    @Override
                    public void contentsChanged(@NotNull VirtualFileEvent virtualFileEvent) {
                        logger.info("Processing contentsChanged event.");
                        if (isProperfile(virtualFileEvent.getFile())) {
                            String path = virtualFileEvent.getSource().toString().substring(7);
                            JsonMaker metadata = JsonMaker.create()
                                    .add("cause", "file_change")
                                    .add("file", new PathResolver()
                                    .getPathRelativeToProject(path));
                            sendMetadata(metadata);
                        }
                    }

                    @Override
                    public void fileCreated(@NotNull VirtualFileEvent virtualFileEvent) {
                        logger.info("Processing fileCreated event");
                        String path = virtualFileEvent.getSource().toString().substring(7);
                        if (isProperfile(virtualFileEvent.getFile())) {
                            if (Files.isDirectory(Paths.get(path))) {
                                JsonMaker metadata = JsonMaker.create()
                                        .add("cause", "folder_create")
                                        .add("file", new PathResolver()
                                                .getPathRelativeToProject(path));
                                sendMetadata(metadata);
                            } else {
                                JsonMaker metadata = JsonMaker.create()
                                        .add("cause", "file_create")
                                        .add("file", new PathResolver()
                                                .getPathRelativeToProject(path));
                                sendMetadata(metadata);
                            }
                        }
                    }

                    @Override
                    public void fileDeleted(@NotNull VirtualFileEvent virtualFileEvent) {
                    }

                    @Override
                    public void fileMoved(@NotNull VirtualFileMoveEvent virtualFileMoveEvent) {
                        logger.info("Processing fileMoved event.");
                        if (isProperfile(virtualFileMoveEvent.getFile())) {
                            String path = virtualFileMoveEvent.getSource().toString().substring(7);
                            if (Files.isDirectory(Paths.get(path))) {
                                JsonMaker metadata = JsonMaker.create()
                                        .add("cause", "folder_move")
                                        .add("file", new PathResolver()
                                                .getPathRelativeToProject(path))
                                        .add("from",
                                                virtualFileMoveEvent.getOldParent().toString());
                                sendMetadata(metadata);
                            } else {
                                JsonMaker metadata = JsonMaker.create()
                                        .add("cause", "file_move")
                                        .add("file", new PathResolver()
                                                .getPathRelativeToProject(path))
                                        .add("from",
                                                virtualFileMoveEvent.getOldParent().toString());
                                sendMetadata(metadata);
                            }
                        }
                    }

                    @Override
                    public void fileCopied(@NotNull VirtualFileCopyEvent virtualFileCopyEvent) {
                        logger.info("Processing fileCopied event.");
                        String path = virtualFileCopyEvent.getSource().toString().substring(7);
                        if (Files.isDirectory(Paths.get(path))) {
                            JsonMaker metadata = JsonMaker.create()
                                    .add("cause", "folder_copy")
                                    .add("file", new PathResolver().getPathRelativeToProject(path))
                                    .add("from", virtualFileCopyEvent.getOriginalFile().toString());
                            sendMetadata(metadata);
                        } else {
                            JsonMaker metadata = JsonMaker.create()
                                    .add("cause", "file_copy")
                                    .add("file", new PathResolver().getPathRelativeToProject(path))
                                    .add("from", virtualFileCopyEvent.getOriginalFile().toString());
                            sendMetadata(metadata);
                        }
                    }

                    @Override
                    public void beforePropertyChange(
                            @NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {

                    }

                    @Override
                    public void beforeContentsChange(@NotNull VirtualFileEvent virtualFileEvent) {

                    }

                    @Override
                    public void beforeFileDeletion(@NotNull VirtualFileEvent virtualFileEvent) {
                        logger.info("Processing fileDeleted event.");
                        if (isProperfile(virtualFileEvent.getFile())) {
                            String path = virtualFileEvent.getFile().getPath();
                            JsonMaker metadata = JsonMaker.create()
                                    .add("cause", "file_delete")
                                    .add("file", new PathResolver().getPathRelativeToProject(path));
                            sendMetadata(metadata);
                        }
                    }

                    @Override
                    public void beforeFileMovement(@NotNull VirtualFileMoveEvent vfme) {

                    }
                });
    }

    private void sendMetadata(JsonMaker metadata) {
        if (!TmcSettingsManager.get().isSpyware()) {
            return;
        }

        Exercise exercise = getExercise();

        if (exercise != null) {
            logger.info("Starting zipping thread for exercise: {0}", exercise);
            SnapshotThread thread = new SnapshotThread(exercise, project, metadata);
            snapshotterThreads.addThread(thread);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public void close() throws IOException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    closed = true;
                    snapshotterThreads.joinAll();
                } catch (InterruptedException ex) {
                }
            }
        });
    }

    public boolean isProperfile(VirtualFile virtualFile) {
        return !(virtualFile.toString().contains(".idea")
                || virtualFile.toString().contains(".iml")
                || virtualFile.toString().contains(".xml")
                || virtualFile.toString().contains("/out/")
                || virtualFile.toString().contains(".txt"));
    }

    private static class SnapshotThread extends Thread {
        private final Exercise exercise;
        private final Project projectInfo;
        private final JsonMaker metadata;

        private SnapshotThread(Exercise exercise, Project projectInfo, JsonMaker metadata) {
            super("Source snapshot");
            this.exercise = exercise;
            this.projectInfo = projectInfo;
            this.metadata = metadata;
        }

        @Override
        public void run() {
            // Note that, being in a thread, this is inherently prone to races that modify the project.
            // For now we just accept that. Not sure if the FileObject API would allow some sort of
            // global locking of the project.
            File projectDir = new File(projectInfo.getBasePath());
            RecursiveZipper.ZippingDecider zippingDecider = new ZippingDeciderWrapper(projectInfo);
            RecursiveZipper zipper = new RecursiveZipper(projectDir, zippingDecider);
            try {
                byte[] data = zipper.zipProjectSources();
                LoggableEvent event = new LoggableEvent(exercise, "code_snapshot", data, metadata);
                SpywareEventManager.add(event);
            } catch (IOException ex) {
                // Warning might be also appro1priate, but this often races with project closing
                // during integration tests, and there warning would cause a dialog to appear,
                // failing the test.
                logger.warn("Error zipping project sources in: " + projectDir, ex);
            }
        }
    }

    private static class ZippingDeciderWrapper implements RecursiveZipper.ZippingDecider {
        private static final long MAX_FILE_SIZE = 100 * 1024; // 100KB

        protected static final String[] BLACKLISTED_FILE_EXTENSIONS = {
                ".min.js",
                ".pack.js",
                ".jar",
                ".war",
                ".mp3",
                ".ogg",
                ".wav",
                ".png",
                ".jpg",
                ".jpeg",
                ".ttf",
                ".eot",
                ".woff"
        };

        private final Project projectInfo;

        public ZippingDeciderWrapper(Project projectInfo) {
            this.projectInfo = projectInfo;
        }

        protected boolean isProbablyBundledBinary(String zipPath) {
            for (String ext : BLACKLISTED_FILE_EXTENSIONS) {
                if (zipPath.endsWith(ext)) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isTooBig(File file) {
            return file.length() > MAX_FILE_SIZE;
        }

        protected boolean hasNoSnapshotFile(File dir) {
            return new File(dir, ".tmcnosnapshot").exists();
        }

        @Override
        public boolean shouldZip(String zipPath) {
            File file = new File(projectInfo.getBasePath(), zipPath);
            if (file.isDirectory()) {
                if (hasNoSnapshotFile(file)) {
                    return false;
                }
            } else {
                if (isProbablyBundledBinary(zipPath)) {
                    return false;
                }
                if (isTooBig(file)) {
                    return false;
                }
            }

            return true;
        }
    }

    public Exercise getExercise() {
        return PathResolver.getExercise(project.getBasePath());
    }

}
