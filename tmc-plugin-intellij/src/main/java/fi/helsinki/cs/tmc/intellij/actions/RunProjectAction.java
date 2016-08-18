package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.RunnerRegistry;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.ide.util.ClassFilter;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunProjectAction extends AnAction {

    private static final Logger logger = LoggerFactory
            .getLogger(RunProjectAction.class);

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
        logger.info("Starting to run project. @RunProjectAction");
        RunManager runManager = RunManager.getInstance(project);
        Module module = ProjectRootManager.getInstance(project)
                .getFileIndex().getModuleForFile(ProjectRootManager
                        .getInstance(project).getContentRootsFromAllModules()[0]);
        if (runManager.getSelectedConfiguration() == null
                || checkConfigurationType(runManager, "Application")) {
            TreeClassChooser chooser = chooseMainClassForProject(project, module);
            if (chooser.getSelected() == null) {
                return;
            }
            createConfiguration(project, module, runManager);
            configApplicationConfiguration(module, runManager, chooser);
        }
        ApplicationConfiguration appCon =
                (ApplicationConfiguration) runManager
                        .getSelectedConfiguration().getConfiguration();
        if (checkForMainClass(project, module, appCon, runManager)) {
            runCreatedConfiguration(project, module, appCon);
        }
    }

    private boolean checkConfigurationType(RunManager runManager, String desired) {
        return !runManager.getSelectedConfiguration()
                .getConfiguration().getType()
                .getDisplayName().equals(desired);
    }

    /**
     * Makes sure the configuration has a defined main class.
     * @param appCon Current configuration
     * @return Whether the class has a valid main class or not
     */

    private boolean checkForMainClass(Project project,
                                      Module module,
                                      ApplicationConfiguration appCon,
                                      RunManager runManager) {
        if (appCon.getMainClass() == null) {
            TreeClassChooser chooser = chooseMainClassForProject(project, module);
            if (chooser.getSelected() == null) {
                return false;
            }
            configApplicationConfiguration(module, runManager, chooser);
        }
        return true;
    }

    /**
     * Creates a run configuration, using the Application template.
     * The created configuration is then added to RunManager.
     */

    @NotNull
    private void createConfiguration(Project project, Module module, RunManager manager) {
        ConfigurationType type = ConfigurationTypeUtil.findConfigurationType("Application");
        RunnerAndConfigurationSettings settings =
                manager.createRunConfiguration(module
                        .getName(), type.getConfigurationFactories()[0]);
        manager.addConfiguration(settings, true);
        manager.setSelectedConfiguration(settings);
    }

    /**
     * Creates an executor, RunnerAndConfigurationSettings implementation,
     * ProgramRunner and an ExecutionEnvironment and uses them to execute
     * the current project.
     */

    private void runCreatedConfiguration(Project project,
                                         Module module,
                                         ApplicationConfiguration appCon) {
        if (checkAtLeastOneProjectIsOpen()) {
            return;
        }
        RunManager runManager = RunManager.getInstance(project);
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        RunnerAndConfigurationSettingsImpl selectedConfiguration =
                getRunnerAndConfigurationSettings(runManager, appCon);
        ProgramRunner runner = getRunner(executor, selectedConfiguration);
        ExecutionEnvironment environment =
                new ExecutionEnvironment(new DefaultRunExecutor(),
                        runner, selectedConfiguration, project);
        try {
            runner.execute(environment);
        } catch (ExecutionException e1) {
            JavaExecutionUtil.showExecutionErrorMessage(e1, "Error", project);
        }
    }

    /**
     * Modifies the run configuration by adding parameters to it,
     * such as a Main class which the user selects.
     * @param chooser Dialog for user to pick Main class.
     */

    @NotNull
    private ApplicationConfiguration configApplicationConfiguration(Module module,
                                                                    RunManager runManager,
                                                                    TreeClassChooser chooser) {
        ApplicationConfiguration appCon =
                (ApplicationConfiguration) runManager
                        .getSelectedConfiguration().getConfiguration();
        appCon.setMainClass(chooser.getSelected());
        appCon.setModule(module);
        return appCon;
    }

    private ProgramRunner getRunner(Executor executor,
                                    RunnerAndConfigurationSettingsImpl selectedConfiguration) {
        return RunnerRegistry.getInstance()
                .getRunner(executor.getId(), selectedConfiguration
                        .getConfiguration());
    }

    @NotNull
    private RunnerAndConfigurationSettingsImpl getRunnerAndConfigurationSettings(
            RunManager runManager, ApplicationConfiguration appCon) {
        return new RunnerAndConfigurationSettingsImpl((RunManagerImpl) runManager,
                appCon, runManager.getSelectedConfiguration().isTemplate());
    }

    private boolean checkAtLeastOneProjectIsOpen() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        if (openProjects.length == 0) {
            return true;
        }
        return false;
    }

    /**
     * Ui for the user to pick the Main class.
     */

    @NotNull
    private TreeClassChooser chooseMainClassForProject(Project project, Module module) {
        TreeClassChooser chooser;
        while (true) {
            TreeClassChooserFactory factory = TreeClassChooserFactory
                    .getInstance(project);
            GlobalSearchScope scope = null;
            scope = GlobalSearchScope.moduleScope(module);
            PsiClass ecClass = JavaPsiFacade.getInstance(project)
                    .findClass("", scope);
            ClassFilter filter = createClassFilter();
            chooser = factory
                    .createInheritanceClassChooser("Choose main class",
                            scope, ecClass, null, filter);
            chooser.showDialog();
            if (chooser.getSelected() == null
                    || chooser.getSelected().findMethodsByName("main", true)
                    .length > 0) {
                break;
            }
        }
        return chooser;
    }

    private ClassFilter createClassFilter() {
        return new ClassFilter() {
            @Override
            public boolean isAccepted(PsiClass psiClass) {
                return psiClass.findMethodsByName("main", false).length > 0;
            }
        };
    }
}