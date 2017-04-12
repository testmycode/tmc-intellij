package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadingService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadingService.class);

    public void runWithNotification(
            final Runnable run, Project project, ProgressWindow progressWindow) {
        logger.info("Processing runWithNotification. @ThreadingService");

        ApplicationManager.getApplication()
                .executeOnPooledThread(
                        () -> ProgressManager.getInstance().runProcess(run, progressWindow));
    }
}
