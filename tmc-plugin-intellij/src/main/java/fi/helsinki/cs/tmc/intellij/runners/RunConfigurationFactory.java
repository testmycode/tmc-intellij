package fi.helsinki.cs.tmc.intellij.runners;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.ide.util.ClassFilter;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunConfigurationFactory {

    private static final Logger logger = LoggerFactory.getLogger(RunConfigurationFactory.class);

    private RunManager runManager;
    private Module module;
    private String configurationType;

    public RunConfigurationFactory(RunManager runManager,
                                   Module module, String configurationType) {
        this.runManager = runManager;
        this.module = module;
        this.configurationType = configurationType;
        logger.info("RunConfigurationFactory initialized.");
    }

    public boolean checkConfigurationType() {
        logger.info("Checking configurationType.");
        return !runManager.getSelectedConfiguration()
                .getConfiguration().getType()
                .getDisplayName().equals(configurationType);
    }

    /**
     * Ui for the user to pick the Main class.
     */

    @NotNull
    public TreeClassChooser chooseMainClassForProject() {
        logger.info("Choosing main class for project.");
        TreeClassChooser chooser;
        Project project = new ObjectFinder().findCurrentProject();
        while (true) {
            TreeClassChooserFactory factory = TreeClassChooserFactory
                    .getInstance(project);
            GlobalSearchScope scope;
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
                logger.info("Choosing main class aborted.");
                break;
            }
        }
        logger.info("Main class chosen successfully.");
        return chooser;
    }

    /**
     * Filters classes shown to the user.
     */

    private ClassFilter createClassFilter() {
        logger.info("Creating classFilter.");
        return new ClassFilter() {
            @Override
            public boolean isAccepted(PsiClass psiClass) {
                return psiClass.findMethodsByName("main", true).length > 0;
            }
        };
    }

    /**
     * Creates a run configuration, using the given template.
     * The created configuration is then added to RunManager.
     */

    @NotNull
    public void createConfiguration() {
        logger.info("Creating configuration.");
        ConfigurationType type = ConfigurationTypeUtil.findConfigurationType(configurationType);
        RunnerAndConfigurationSettings settings =
                runManager.createRunConfiguration(module
                        .getName(), type.getConfigurationFactories()[0]);
        logger.info("Adding configuration to RunManager.");
        runManager.addConfiguration(settings, true);
        logger.info("Setting created configuration to be the selected.");
        runManager.setSelectedConfiguration(settings);
    }

    /**
     * Modifies the run configuration by adding parameters to it,
     * such as a Main class which the user selects.
     */

    @NotNull
    public ApplicationConfiguration configApplicationConfiguration(TreeClassChooser chooser) {
        if (configurationType.equals("Application")) {
            logger.info("Creating ApplicationConfiguration.");
            ApplicationConfiguration appCon =
                    (ApplicationConfiguration) runManager
                            .getSelectedConfiguration().getConfiguration();
            logger.info("Setting main class and module to " + configurationType);
            appCon.setMainClass(chooser.getSelected());
            appCon.setModule(module);
            return appCon;
        }
        logger.info("No applicable configurationType was found.");
        return null;
    }

    public void createRunner() {
        if (configurationType.equals("Application")) {
            logger.info("Creating runner for type " + configurationType);
            ApplicationConfiguration appCon =
                    (ApplicationConfiguration) runManager
                            .getSelectedConfiguration().getConfiguration();
            if (checkForMainClass(appCon)) {
                logger.info("Main class found, starting to execute project.");
                ProjectExecutor executor = new ProjectExecutor();
                executor.executeConfiguration(new ObjectFinder().findCurrentProject(), appCon);
            }
        }
    }

    /**
     * Makes sure the configuration has a defined main class.
     * This method only works for the Application type of configuration,
     * For other types it may be necessary to create their own.
     * @param appCon Current configuration
     * @return Whether the class has a valid main class or not
     */

    private boolean checkForMainClass(ApplicationConfiguration appCon) {
        if (appCon.getMainClass() == null) {
            logger.info("No main class was found, prompting user to choose one.");
            TreeClassChooser chooser = chooseMainClassForProject();
            if (chooser.getSelected() == null) {
                return false;
            }
            configApplicationConfiguration(chooser);
        }
        return true;
    }
}
