package fi.helsinki.cs.tmc.intellij.spyware;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.utilities.JsonMaker;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
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
    private String projectPath;
    private boolean closed;
    private ActiveThreadSet snapshotterThreads;
    private Project project;
    private static VirtualFileListener listener;

    public SpywareFileListener(Project project) {
        this.project = project;
        this.projectPath = project.getBasePath();
        this.snapshotterThreads = new ActiveThreadSet();
    }

    public void removeListener() {
        if (listener == null) {
            return;
        }
        VirtualFileManager.getInstance()
                .removeVirtualFileListener(listener);

    }

    public void createAndAddListener() {
        if (listener == null) {
            listener = getVirtualFileListener();
            VirtualFileManager.getInstance()
                    .addVirtualFileListener(listener);
        } else {
            VirtualFileManager.getInstance()
                    .removeVirtualFileListener(listener);
            listener = getVirtualFileListener();
            VirtualFileManager.getInstance()
                    .addVirtualFileListener(listener);
        }
        this.projectPath = project.getBasePath();
    }

    @NotNull
    private VirtualFileListener getVirtualFileListener() {
        return new VirtualFileListener() {
            @Override
            public void propertyChanged(
                    @NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {
                logger.info("Processing propertyChanged event.");
                if (!isProperfile(virtualFilePropertyEvent.getFile())) {
                    return;
                }

                String path = virtualFilePropertyEvent.getSource()
                        .toString().substring(7);

                String folderOrFile = setFolderOrFile(path, "rename");

                JsonMaker metadata = JsonMaker.create()
                        .add("cause", folderOrFile)
                        .add("file", new PathResolver()
                                .getPathRelativeToProject(path))
                        .add("previous_name", virtualFilePropertyEvent
                                .getOldValue().toString());
                sendMetadata(metadata);
            }

            @Override
            public void contentsChanged(@NotNull VirtualFileEvent virtualFileEvent) {
                logger.info("Processing contentsChanged event.");
                if (!isProperfile(virtualFileEvent.getFile())) {
                    return;
                }

                String path = virtualFileEvent.getSource().toString().substring(7);
                JsonMaker metadata = JsonMaker.create()
                        .add("cause", "file_change")
                        .add("file", new PathResolver()
                                .getPathRelativeToProject(path));
                sendMetadata(metadata);
            }

            @Override
            public void fileCreated(@NotNull VirtualFileEvent virtualFileEvent) {
                logger.info("Processing fileCreated event");

                if (!isProperfile(virtualFileEvent.getFile())) {
                    return;
                }

                String path = virtualFileEvent.getSource().toString().substring(7);
                prepareMetaData("create", path);
            }

            @Override
            public void fileMoved(@NotNull VirtualFileMoveEvent virtualFileMoveEvent) {
                logger.info("Processing fileMoved event.");
                if (!isProperfile(virtualFileMoveEvent.getFile())) {
                    return;
                }

                String path = virtualFileMoveEvent.getSource().toString().substring(7);
                prepareMetaData("move", path);
            }

            private void prepareMetaData(String action, String path) {
                String folderOrFile = setFolderOrFile(path, action);

                JsonMaker metadata = JsonMaker.create()
                        .add("cause", folderOrFile)
                        .add("file", new PathResolver()
                                .getPathRelativeToProject(path));
                sendMetadata(metadata);
            }

            @Override
            public void fileCopied(@NotNull VirtualFileCopyEvent virtualFileCopyEvent) {
                logger.info("Processing fileCopied event.");
                String path = virtualFileCopyEvent.getSource().toString().substring(7);

                String folderOrFile = setFolderOrFile(path, "copy");

                JsonMaker metadata = JsonMaker.create()
                        .add("cause", folderOrFile)
                        .add("file", new PathResolver().getPathRelativeToProject(path))
                        .add("from", virtualFileCopyEvent.getOriginalFile().toString());
                sendMetadata(metadata);
            }

            private String setFolderOrFile(String path, String action) {
                if (Files.isDirectory(Paths.get(path))) {
                    return "folder_" + action;
                }

                return "file_" + action;
            }

            @Override
            public void beforePropertyChange(
                    @NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {
            }

            @Override
            public void beforeFileDeletion(@NotNull VirtualFileEvent virtualFileEvent) {
                logger.info("Processing fileDeleted event.");
                if (!isProperfile(virtualFileEvent.getFile())) {
                    return;
                }

                String path = virtualFileEvent.getFile().getPath();
                JsonMaker metadata = JsonMaker.create()
                        .add("cause", "file_delete")
                        .add("file", new PathResolver().getPathRelativeToProject(path));
                sendMetadata(metadata);
            }

            @Override
            public void beforeFileMovement(@NotNull VirtualFileMoveEvent vfme) {

            }

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent virtualFileEvent) {

            }

            @Override
            public void beforeContentsChange(@NotNull VirtualFileEvent virtualFileEvent) {

            }
        };
    }

    private void sendMetadata(JsonMaker metadata) {
        if (!TmcSettingsManager.get().isSpyware()
                || !new CourseAndExerciseManager().isCourseInDatabase(PathResolver
                .getCourseName(projectPath))) {
            return;
        }

        Exercise exercise = getExercise();
        if (exercise == null) {
            return;
        }

        logger.info("Starting zipping thread for exercise: {0}", exercise);
        SnapshotThread thread = new SnapshotThread(exercise,  projectPath, metadata);
        snapshotterThreads.addThread(thread);
        thread.setDaemon(true);
        thread.start();
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
                || virtualFile.toString().contains(".txt")
                || virtualFile.toString().contains("/build")
                || virtualFile.toString().contains(".zip")
                || virtualFile.toString().contains(".jar"));
    }

    private static class SnapshotThread extends Thread {
        private final Exercise exercise;
        private final String  projectPathInfo;
        private final JsonMaker metadata;

        private SnapshotThread(Exercise exercise, String  projectPathInfo, JsonMaker metadata) {
            super("Source snapshot");
            this.exercise = exercise;
            this. projectPathInfo =  projectPathInfo;
            this.metadata = metadata;
        }

        @Override
        public void run() {
            // Note that, being in a thread, this is inherently prone to races that modify the  projectPath.
            // For now we just accept that. Not sure if the FileObject API would allow some sort of
            // global locking of the  projectPath.
            File  projectPathDir = new File( projectPathInfo);
            RecursiveZipper
                    .ZippingDecider zippingDecider = new ZippingDeciderWrapper(projectPathInfo);
            RecursiveZipper zipper = new RecursiveZipper( projectPathDir, zippingDecider);
            try {
                byte[] data = zipper.zipProjectSources();
                LoggableEvent event = new LoggableEvent(exercise, "code_snapshot", data, metadata);
                SpywareEventManager.add(event);
            } catch (IOException ex) {
                // Warning might be also appro1priate, but this often races with  projectPath closing
                // during integration tests, and there warning would cause a dialog to appear,
                // failing the test.
                logger.warn("Error zipping  projectPath sources in: " +  projectPathDir, ex);
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

        private final String  projectPathInfo;

        public ZippingDeciderWrapper(String projectPathInfo) {
            this. projectPathInfo = projectPathInfo;
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
            File file = new File( projectPathInfo, zipPath);
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
        return PathResolver.getExercise( projectPath);
    }

}
