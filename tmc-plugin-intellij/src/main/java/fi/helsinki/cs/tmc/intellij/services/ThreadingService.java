package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadingService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadingService.class);

    public static void runWithNotification(final Runnable run, String title, Project project) {
        logger.info("Processing runWithNotification. @ThreadingService");

        final ProgressWindow progressWindow = new ProgressWindow(false, true, project);
        progressWindow.setIndeterminate(true);
        progressWindow.setTitle(title);
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ProgressManager.getInstance().runProcess(run, progressWindow);
            }
        });
    }


}
