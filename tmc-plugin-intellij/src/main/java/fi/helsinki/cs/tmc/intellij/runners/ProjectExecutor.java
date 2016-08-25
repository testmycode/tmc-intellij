package fi.helsinki.cs.tmc.intellij.runners;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerRegistry;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ProjectExecutor.class);

    public ProjectExecutor() {
        logger.info("ProjectExecutor initialized.");
    }

    public void executeConfiguration(Project project,
                                          ModuleBasedConfiguration appCon) {
        if (checkAtLeastOneProjectIsOpen()) {
            logger.warn("No open projects found, can't execute the project.");
            return;
        }
        logger.info("Starting to build execution environment.");
        RunManager runManager = RunManager.getInstance(project);
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        RunnerAndConfigurationSettingsImpl selectedConfiguration =
                getApplicationRunnerAndConfigurationSettings(runManager, appCon);
        ProgramRunner runner = getRunner(executor, selectedConfiguration);
        logger.info("Creating ExecutionEnvironment.");
        ExecutionEnvironment environment =
                new ExecutionEnvironment(new DefaultRunExecutor(),
                        runner, selectedConfiguration, project);
        try {
            logger.info("Executing project.");
            runner.execute(environment);
        } catch (ExecutionException e1) {
            JavaExecutionUtil.showExecutionErrorMessage(e1, "Error", project);
        }
    }

    @NotNull
    private RunnerAndConfigurationSettingsImpl getApplicationRunnerAndConfigurationSettings(
            RunManager runManager, ModuleBasedConfiguration appCon) {
        logger.info("Getting RunnerAndConfigurationSettings implementation.");
        return new RunnerAndConfigurationSettingsImpl((RunManagerImpl) runManager,
                appCon, runManager.getSelectedConfiguration().isTemplate());
    }

    private ProgramRunner getRunner(Executor executor,
                                    RunnerAndConfigurationSettingsImpl selectedConfiguration) {
        logger.info("Getting ProgramRunner.");
        return RunnerRegistry.getInstance()
                .getRunner(executor.getId(), selectedConfiguration
                        .getConfiguration());
    }

    private boolean checkAtLeastOneProjectIsOpen() {
        logger.info("Checking that at least one project is open.");
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        if (openProjects.length == 0) {
            return true;
        }
        return false;
    }
}
