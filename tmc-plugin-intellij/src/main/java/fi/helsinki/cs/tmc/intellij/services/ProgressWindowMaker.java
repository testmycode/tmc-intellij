package fi.helsinki.cs.tmc.intellij.services;

import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;

public class ProgressWindowMaker {

    public static ProgressWindow make(String title, Project project) {
        ProgressWindow progressWindow = new ProgressWindow(true, true, project);
        progressWindow.setIndeterminate(true);
        progressWindow.setTitle(title);

        return progressWindow;
    }
}


