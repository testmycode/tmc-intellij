package fi.helsinki.cs.tmc.intellij.spyware;


import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.spyware.HostInformationGenerator;

import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ActivateSpywareListeners {

    private static final Logger logger = LoggerFactory.getLogger(TextInputListener.class);

    private Project project;

    public ActivateSpywareListeners(Project project) {
        logger.info("Activating spyware listeners.");
        this.project = project;
    }

    public void activateListeners() {
        if (isCourseInDatabase(project)) {
            new HostInformationGenerator().updateHostInformation(SpywareEventManager.get());
            new SpywareRunListener(project);
            new SpywareFileListener(project).createAndAddListener();
            new SpywareTabListener(project);
        } else {
            new SpywareFileListener(project).removeListener();
        }
    }

    public void removeListeners() {
        logger.info("Trying to remove file listeners and close it.");
        SpywareFileListener listener = new SpywareFileListener(project);
        listener.removeListener();
        try {
            listener.close();
        } catch (IOException e) {
            logger.warn("Failed to close listener.", e.getStackTrace());
        }
    }

    private boolean isCourseInDatabase(Project project) {
        return new CourseAndExerciseManager()
                .isCourseInDatabase(PathResolver
                        .getCourseName(project.getBasePath()));
    }
}
