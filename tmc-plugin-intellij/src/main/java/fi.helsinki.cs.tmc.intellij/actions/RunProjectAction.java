package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.runners.RunProject;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunProjectAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(RunProjectAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        runProject(anActionEvent.getProject());
    }

    /**
     * Main run method, readies project module for running.
     * First it finds current module, then checks, whether it has a
     * valid runconfiguration or not. If it does not, it creates one, otherwise
     * current configuration is run as is as long as it has a main class.
     */

    private void runProject(Project project) {
        logger.info("Run project action called.");
        logger.info("Getting RunManager.");
        RunManager runManager = RunManager.getInstance(project);
        Module module = getModule(project);
        String configurationType = getConfigurationType();
        logger.info("Creating RunProject object.");
        RunProject runner = new RunProject(runManager, module, configurationType);
    }

    /**
     * Use this to determine the configuration type.
     * The new configuration type also needs to be configured in RunConfigurationFactory.
     * @return The name of the configuration type to be used.
     */
    @NotNull
    private String getConfigurationType() {
        logger.info("Getting configurationtype.");
        return "Application";
    }

    private Module getModule(Project project) {
        logger.info("Trying to find module.");
        return ProjectRootManager.getInstance(project)
                .getFileIndex().getModuleForFile(ProjectRootManager
                        .getInstance(project).getContentRootsFromAllModules()[0]);
    }
}