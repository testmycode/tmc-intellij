package fi.helsinki.cs.tmc.intellij.holders;

import fi.helsinki.cs.tmc.intellij.ui.projectlist.ProjectListManager;

/**
 * Holds an instance of ProjectListManager as a singleton
 */
public class ProjectListManagerHolder {

    private static ProjectListManager projectListManager;

    private ProjectListManagerHolder() {}

    public static synchronized ProjectListManager get() {
        if (projectListManager == null) {
            projectListManager = new ProjectListManager();
        }
        return projectListManager;
    }

    public static synchronized void setup() {
        if (projectListManager == null) {
            projectListManager = new ProjectListManager();
        }
    }

    public static synchronized void set(ProjectListManager newProjectListManager) {
        projectListManager = newProjectListManager;
    }
}
