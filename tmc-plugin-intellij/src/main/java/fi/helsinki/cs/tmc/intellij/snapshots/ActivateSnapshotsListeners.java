package fi.helsinki.cs.tmc.intellij.snapshots;

import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.snapshots.*;

import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ActivateSnapshotsListeners {

    private static final Logger logger = LoggerFactory.getLogger(TextInputListener.class);

    private final Project project;

    public ActivateSnapshotsListeners(Project project) {
        logger.info("Activating snapshots listeners.");
        this.project = project;
    }

    public void activateListeners() {
        if (isCourseInDatabase(project)) {
                new HostInformationGenerator().updateHostInformation(SnapshotsEventManager.get());
            new SnapshotsRunListener(project);
            new SnapshotsFileListener(project).createAndAddListener();
            new SnapshotsTabListener(project);
        } else {
            new SnapshotsFileListener(project).removeListener();
        }
    }

    public void removeListeners() {
        logger.info("Trying to remove file listeners and close it.");
        SnapshotsFileListener listener = new SnapshotsFileListener(project);
        listener.removeListener();

        try {
            listener.close();
        } catch (IOException e) {
            logger.warn("Failed to close listener.", e.getStackTrace());
        }
    }

    private boolean isCourseInDatabase(Project project) {
        return new CourseAndExerciseManager()
                .isCourseInDatabase(PathResolver.getCourseName(project.getBasePath()));
    }
}
