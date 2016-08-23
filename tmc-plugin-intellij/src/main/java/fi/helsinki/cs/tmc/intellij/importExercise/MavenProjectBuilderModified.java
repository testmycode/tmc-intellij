package fi.helsinki.cs.tmc.intellij.importExercise;

import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import icons.MavenIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProviderImpl;
import com.intellij.openapi.externalSystem.service.project.IdeUIModifiableModelsProvider;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.*;
import org.jetbrains.idea.maven.utils.*;

import java.util.*;

/**
 * Created by melchan on 23.8.2016.
 */
public class MavenProjectBuilderModified {

    private static class Parameters {
        private Project myProjectToUpdate;

        private MavenGeneralSettings myGeneralSettingsCache;
        private MavenImportingSettings myImportingSettingsCache;

        private VirtualFile myImportRoot;
        private List<VirtualFile> myFiles;
        private List<String> myProfiles = new ArrayList<>();
        private List<String> myActivatedProfiles = new ArrayList<>();
        private MavenExplicitProfiles mySelectedProfiles = MavenExplicitProfiles.NONE;

        private MavenProjectsTree myMavenProjectTree;
        private List<MavenProject> mySelectedProjects;

        private boolean myOpenModulesConfigurator;
    }

    public static void commit(Project project,
                              ModifiableModuleModel model,
                              ModulesProvider modulesProvider,
                              ModifiableArtifactModel artifactModel) {
        Parameters myParameters = new Parameters();

        MavenWorkspaceSettings settings = MavenWorkspaceSettingsComponent.getInstance(project).getSettings();

        settings.generalSettings = getGeneralSettings();
        settings.importingSettings = getImportingSettings();

        String settingsFile = System.getProperty("idea.maven.import.settings.file");
        if (!StringUtil.isEmptyOrSpaces(settingsFile)) {
            settings.generalSettings.setUserSettingsFile(settingsFile.trim());
        }

        MavenExplicitProfiles selectedProfiles = myParameters.mySelectedProfiles;

        String enabledProfilesList = System.getProperty("idea.maven.import.enabled.profiles");
        String disabledProfilesList = System.getProperty("idea.maven.import.disabled.profiles");
        if (enabledProfilesList != null || disabledProfilesList != null) {
            selectedProfiles = selectedProfiles.clone();
            appendProfilesFromString(selectedProfiles.getEnabledProfiles(), enabledProfilesList);
            appendProfilesFromString(selectedProfiles.getDisabledProfiles(), disabledProfilesList);
        }

        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

        manager.setIgnoredState(myParameters.mySelectedProjects, false);

        manager.addManagedFilesWithProfiles(MavenUtil.collectFiles(myParameters.mySelectedProjects), selectedProfiles);
        manager.waitForReadingCompletion();

//        boolean isFromUI = model != null;
//        return manager.importProjects(isFromUI
//                ? new IdeUIModifiableModelsProvider(project, model, (ModulesConfigurator)modulesProvider, artifactModel)
//                : new IdeModifiableModelsProviderImpl(project));

    }

    private static void appendProfilesFromString(JamAnnotationAttributeMeta.Collection<String> selectedProfiles, String profilesList) {
        if (profilesList == null) return;

        for (String profile : StringUtil.split(profilesList, ",")) {
            String trimmedProfileName = profile.trim();
            if (!trimmedProfileName.isEmpty()) {
                selectedProfiles.add(trimmedProfileName);
            }
        }
    }

}
