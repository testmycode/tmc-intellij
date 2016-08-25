package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;

public class ProgressWindowMaker {

    public static ProgressWindow make(String title, Project project,
                                      boolean cancelable, boolean hidable) {
        ProgressWindow progressWindow = new ProgressWindow(cancelable, hidable, project);
        progressWindow.setIndeterminate(true);
        progressWindow.setTitle(title);
        progressWindow.setDelayInMillis(500);

        return progressWindow;
    }
}


