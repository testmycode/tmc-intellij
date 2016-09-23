package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressWindowMaker {

    private static final Logger logger = LoggerFactory.getLogger(ProgressWindow.class);

    public static ProgressWindow make(
            String title,
            Project project,
            boolean cancelable,
            boolean hidable,
            boolean indeterminate) {
        logger.info("Creating progress window. @ProgressWindowMaker");
        ProgressWindow progressWindow = new ProgressWindow(cancelable, hidable, project);
        progressWindow.setIndeterminate(indeterminate);
        progressWindow.setTitle(title);
        progressWindow.setDelayInMillis(500);

        return progressWindow;
    }
}
