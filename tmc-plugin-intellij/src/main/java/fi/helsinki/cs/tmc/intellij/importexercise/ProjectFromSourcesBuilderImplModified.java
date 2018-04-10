
/*
 * @author Eugene Zhuravlev.
 *         Date: Jul 17, 2007
 */

package fi.helsinki.cs.tmc.intellij.importexercise;

import static com.intellij.ide.util.projectWizard.importSources.impl.ProjectFromSourcesBuilderImpl.getPackagePrefix;

import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;

import com.intellij.ide.util.importProject.LibraryDescriptor;
import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ModuleInsight;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.ExistingModuleLoader;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.IdeaModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 *  this code is modified form intellij code. url below
 *  original class: ProjectFromSourcesBuilderImpl
 *  https://github.com/JetBrains/intellij-community/blob/1fb6ce35950512b62c1f4a397d907de8b702d762/java/idea-ui/src/com/intellij/ide/util/projectWizard/importSources/impl/ProjectFromSourcesBuilderImpl.java
 */

public class ProjectFromSourcesBuilderImplModified {
    private static final Logger logger =
            LoggerFactory.getLogger(ProjectFromSourcesBuilderImplModified.class);

    /*
     * Collects info to build module and libraries and then creates them.
     * In original class only creates them from info gotten from wizard.
     * This is modified to work only with project and root path info.
     * @param project where these modules and libraries should go.
     * @param path project root dir
     * @return
     */
    public static void commit(@NotNull final Project project, String path) {
        logger.info("Starting commit in ProjectFromSourcesBuilderImplModified");
        ProjectDescriptor projectDescriptor =
                JavaProjectDescriptor.create(path, getIgnoredFileNamesSet());

        ModifiableModelsProvider modelsProvider = new IdeaModifiableModelsProvider();
        final LibraryTable.ModifiableModel projectLibraryTable =
                modelsProvider.getLibraryTableModifiableModel(project);
        final Map<LibraryDescriptor, Library> projectLibs = new HashMap<>();
        final List<Module> result = new ArrayList<>();
        try {
            AccessToken token = WriteAction.start();
            try {
                // create project-level libraries
                logger.info("Create project-level libraries.");
                for (LibraryDescriptor lib : projectDescriptor.getLibraries()) {
                    final Collection<File> files = lib.getJars();
                    final Library projectLib = projectLibraryTable.createLibrary(lib.getName());
                    final Library.ModifiableModel libraryModel = projectLib.getModifiableModel();
                    for (File file : files) {
                        libraryModel.addRoot(
                                VfsUtil.getUrlForLibraryRoot(file), OrderRootType.CLASSES);
                    }
                    logger.info("Saving library {} to library model", lib.getName());
                    libraryModel.commit();
                    projectLibs.put(lib, projectLib);
                }

                projectLibraryTable.commit();

            } finally {
                token.finish();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            new ErrorMessageService().showErrorMessageWithExceptionDetails(e, "Error adding module to project", true);
        }

        final Map<ModuleDescriptor, Module> descriptorToModuleMap = new HashMap<>();

        try {
            AccessToken token = WriteAction.start();
            logger.info("Starts creating modules");
            try {
                final ModifiableModuleModel moduleModel =
                        ModuleManager.getInstance(project).getModifiableModel();
                logger.info("Goes trough module descriptions to build module");
                for (final ModuleDescriptor moduleDescriptor : projectDescriptor.getModules()) {
                    final Module module;
                    if (moduleDescriptor.isReuseExistingElement()) {
                        final ExistingModuleLoader moduleLoader =
                                ExistingModuleLoader.setUpLoader(
                                        FileUtil.toSystemIndependentName(
                                                moduleDescriptor.computeModuleFilePath()));
                        module = moduleLoader.createModule(moduleModel);
                    } else {
                        module =
                                createModule(
                                        projectDescriptor,
                                        moduleDescriptor,
                                        projectLibs,
                                        moduleModel);
                    }
                    result.add(module);
                    descriptorToModuleMap.put(moduleDescriptor, module);
                }
                logger.info("Saving up created modules");
                moduleModel.commit();

            } finally {
                token.finish();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            new ErrorMessageService().showErrorMessageWithExceptionDetails(e, "Error adding module to project", true);
        }
        logger.info("ending commit in ProjectFromSourcesBuilderImplModified");
        //return result;
    }

    private static Module createModule(
            ProjectDescriptor projectDescriptor,
            final ModuleDescriptor descriptor,
            final Map<LibraryDescriptor, Library> projectLibs,
            final ModifiableModuleModel moduleModel) {

        logger.info("Starting createModule in ProjectFromSourcesBuilderImplModified");
        final String moduleFilePath = descriptor.computeModuleFilePath();
        ModuleBuilder.deleteModuleFile(moduleFilePath);

        final Module module =
                moduleModel.newModule(moduleFilePath, descriptor.getModuleType().getId());
        final ModifiableRootModel modifiableModel =
                ModuleRootManager.getInstance(module).getModifiableModel();
        setupRootModel(projectDescriptor, descriptor, modifiableModel, projectLibs);
        descriptor.updateModuleConfiguration(module, modifiableModel);
        modifiableModel.commit();
        logger.info("ending createModule in ProjectFromSourcesBuilderImplModified");
        return module;
    }

    private static void setupRootModel(
            ProjectDescriptor projectDescriptor,
            final ModuleDescriptor descriptor,
            final ModifiableRootModel rootModel,
            final Map<LibraryDescriptor, Library> projectLibs) {
        final CompilerModuleExtension compilerModuleExtension =
                rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);
        rootModel.inheritSdk();

        //Module root model seems to store .iml files root dependencies. (src, test, lib)
        logger.info("Starting setupRootModel in ProjectFromSourcesBuilderImplModified");
        final Set<File> contentRoots = descriptor.getContentRoots();
        for (File contentRoot : contentRoots) {
            final LocalFileSystem lfs = LocalFileSystem.getInstance();
            VirtualFile moduleContentRoot =
                    lfs.refreshAndFindFileByPath(
                            FileUtil.toSystemIndependentName(contentRoot.getPath()));
            if (moduleContentRoot != null) {
                final ContentEntry contentEntry = rootModel.addContentEntry(moduleContentRoot);
                final Collection<DetectedSourceRoot> sourceRoots =
                        descriptor.getSourceRoots(contentRoot);
                for (DetectedSourceRoot srcRoot : sourceRoots) {
                    final String srcpath =
                            FileUtil.toSystemIndependentName(srcRoot.getDirectory().getPath());
                    final VirtualFile sourceRoot = lfs.refreshAndFindFileByPath(srcpath);
                    if (sourceRoot != null) {
                        contentEntry.addSourceFolder(
                                sourceRoot,
                                shouldBeTestRoot(srcRoot.getDirectory()),
                                getPackagePrefix(srcRoot));
                    }
                }
            }
        }
        logger.info("Inherits compiler output path from project");
        compilerModuleExtension.inheritCompilerOutputPath(true);

        logger.info("Starting to create module level libraries");
        final LibraryTable moduleLibraryTable = rootModel.getModuleLibraryTable();
        for (LibraryDescriptor libDescriptor :
                ModuleInsight.getLibraryDependencies(
                        descriptor, projectDescriptor.getLibraries())) {
            final Library projectLib = projectLibs.get(libDescriptor);
            if (projectLib != null) {
                rootModel.addLibraryEntry(projectLib);
            } else {
                // add as module library
                final Collection<File> jars = libDescriptor.getJars();
                for (File file : jars) {
                    Library library = moduleLibraryTable.createLibrary();
                    Library.ModifiableModel modifiableModel = library.getModifiableModel();
                    modifiableModel.addRoot(
                            VfsUtil.getUrlForLibraryRoot(file), OrderRootType.CLASSES);
                    modifiableModel.commit();
                }
            }
        }
        logger.info("Ending setupRootModel in ProjectFromSourcesBuilderImplModified");
    }

    private static boolean shouldBeTestRoot(final File srcRoot) {
        logger.info("Starting shouldBeTestRoot in ProjectFromSourcesBuilderImplModified");
        if (isTestRootName(srcRoot.getName())) {
            return true;
        }
        final File parentFile = srcRoot.getParentFile();
        logger.info("Ending shouldBeTestRoot in ProjectFromSourcesBuilderImplModified");
        return parentFile != null && isTestRootName(parentFile.getName());
    }

    private static boolean isTestRootName(final String name) {
        return "test".equalsIgnoreCase(name)
                || "tests".equalsIgnoreCase(name)
                || "testSource".equalsIgnoreCase(name)
                || "testSources".equalsIgnoreCase(name)
                || "testSrc".equalsIgnoreCase(name)
                || "tst".equalsIgnoreCase(name);
    }

    private static Set<String> getIgnoredFileNamesSet() {
        Set<String> ignoredNames = new HashSet<>();
        ignoredNames.add("Target");
        ignoredNames.add("target");
        ignoredNames.add("out");
        ignoredNames.add("Out");
        return ignoredNames;
    }
}
