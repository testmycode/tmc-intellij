package fi.helsinki.cs.tmc.intellij.io;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.util.importProject.*;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.ide.util.projectWizard.importSources.*;
import com.intellij.ide.util.projectWizard.importSources.impl.ProjectFromSourcesBuilderImpl;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectManagerImpl;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.intellij.diff.tools.simple.ThreesideTextDiffViewerEx.LOG;
import static com.intellij.ide.impl.NewProjectUtil.applyJdkToProject;
import static com.intellij.ide.util.projectWizard.importSources.impl.ProjectFromSourcesBuilderImpl.getPackagePrefix;

public class ExerciseImport {

    //NewProjectUtil from intellij has been used as basis when making this method
    public static void importExercise(String path) throws IOException{

        final String projectFilePath = path;


        //TODO separate to method about getting projectName

        String projectName;
        String[] parsedPath = PathResolver.getCourseAndExerciseName(path);
        projectName = parsedPath[parsedPath.length - 1];

        //creates Project class
        final Project newProject; //= ProjectManagerImpl.getInstanceEx().newProject(path.toString(), path.toString(), true, false);
        //ProjectRootManagerImpl projectRootManager = new ProjectRootManagerImpl(newProject);
        final WizardContext wizardContext = new WizardContext(null, null);
        ProjectBuilder projectBuilder = new ProjectFromSourcesBuilderImpl(wizardContext, null);

        try {
            File projectDir = new File(projectFilePath).getParentFile();
            LOG.assertTrue(projectDir != null, "Cannot create project in '" + projectFilePath + "': no parent file exists");
            FileUtil.ensureExists(projectDir);

            final File ideaDir = new File(projectFilePath, Project.DIRECTORY_STORE_FOLDER);
            FileUtil.ensureExists(ideaDir);

            if (projectBuilder == null || !projectBuilder.isUpdate()) {
                String name = projectName;
                newProject = projectBuilder == null
                        ? ProjectManagerImpl.getInstanceEx().newProject(path.toString(), path.toString(), true, false)
                        : projectBuilder.createProject(name, projectFilePath);
            }
            else {
                System.out.println("Projectbuilder on null tai isUpdate rivi 82");
                return;
            }


            final Sdk jdk = ProjectJdkTable.getInstance().findMostRecentSdkOfType(JavaSdk.getInstance());
            if (jdk != null) {
                CommandProcessor.getInstance().executeCommand(newProject, () -> ApplicationManager.getApplication().runWriteAction(() -> applyJdkToProject(newProject, jdk)), null, null);
            }



            final String compileOutput = StringUtil.endsWithChar(path, '/') ? path + "out" : path + "/out";;
            CommandProcessor.getInstance().executeCommand(newProject, () -> ApplicationManager.getApplication().runWriteAction(() -> {
                String canonicalPath = compileOutput;
                try {
                    canonicalPath = FileUtil.resolveShortWindowsName(compileOutput);
                }
                catch (IOException e) {
                    //file doesn't exist
                }

                canonicalPath = FileUtil.toSystemIndependentName(canonicalPath);
                CompilerProjectExtension.getInstance(newProject).setCompilerOutputUrl(VfsUtilCore.pathToUrl(canonicalPath));
            }), null, null);

            if (!ApplicationManager.getApplication().isUnitTestMode()) {
                // java.lang.RuntimeException: java.io.IOException: getOutputStream() must not be called against a directory: file:///home/melchan/kökkö/TMCProjects/tmc-intellij-ohpe-k2016/viikko01-Viikko01_004.RobottiOhjain
                newProject.save();
            }
            if (projectBuilder != null) {
                commit(newProject, path, ModulesProvider.EMPTY_MODULES_PROVIDER);
            }


            ProjectUtil.updateLastProjectLocation(projectFilePath);
            newProject.save();
            if (!ApplicationManager.getApplication().isUnitTestMode()) {
                newProject.save();
            }
        }
        finally {
            if (projectBuilder != null) {
                projectBuilder.cleanup();
            }
        }
    }

    private static List<Module> commit(@NotNull final Project project, String path, final ModulesProvider modulesProvi) {
        File contentFile = new File(path);

        //TODO Detector detects and fills projectDescriptor
        ProjectDescriptor prjctDescriptor = new ProjectDescriptor();
        List<ModuleDescriptor> moduleList = new ArrayList<>();
        List<LibraryDescriptor> libraryList = new ArrayList<>();
        prjctDescriptor.setModules(moduleList);
        prjctDescriptor.setLibraries(libraryList);

        Collection<JavaModuleSourceRoot> suggestedRoots = JavaSourceRootDetectionUtil.suggestRoots(contentFile);

        ModuleDescriptor moduleDescriptor = new ModuleDescriptor(contentFile, new JavaModuleType(), suggestedRoots);
        moduleList.add(moduleDescriptor);

        



        final Map<ProjectStructureDetector, ProjectDescriptor> myProjectDescriptors = new LinkedHashMap<>();
        for (ProjectStructureDetector detector : ProjectStructureDetector.EP_NAME.getExtensions()) {
            myProjectDescriptors.put(detector, new ProjectDescriptor());

        }

        ModifiableModelsProvider modelsProvider = new IdeaModifiableModelsProvider();
        final LibraryTable.ModifiableModel projectLibraryTable = modelsProvider.getLibraryTableModifiableModel(project);
        final Map<LibraryDescriptor, Library> projectLibs = new HashMap<>();
        final List<Module> result = new ArrayList<>();
        try {
            AccessToken token = WriteAction.start();
            try {
                // create project-level libraries
                for (ProjectDescriptor projectDescriptor : myProjectDescriptors.values()) {
                    System.out.println("kirjastot " + projectDescriptor.getLibraries().size());
                    System.out.println("Moduulit " + projectDescriptor.getModules().size());
                    for (LibraryDescriptor lib : projectDescriptor.getLibraries()) {
                        final Collection<File> files = lib.getJars();
                        final Library projectLib = projectLibraryTable.createLibrary(lib.getName());
                        final Library.ModifiableModel libraryModel = projectLib.getModifiableModel();
                        for (File file : files) {
                            libraryModel.addRoot(VfsUtil.getUrlForLibraryRoot(file), OrderRootType.CLASSES);
                        }
                        libraryModel.commit();
                        projectLibs.put(lib, projectLib);
                    }
                }
                if (!fromProjectStructure) {
                    projectLibraryTable.commit();
                }
            }
            finally {
                token.finish();
            }
        }
        catch (Exception e) {
            LOG.info(e);
            Messages.showErrorDialog(IdeBundle.message("error.adding.module.to.project", e.getMessage()), IdeBundle.message("title.add.module"));
        }

        final Map<ModuleDescriptor, Module> descriptorToModuleMap = new HashMap<>();

        try {
            AccessToken token = WriteAction.start();
            try {
                final ModifiableModuleModel moduleModel = fromProjectStructure ? model : ModuleManager.getInstance(project).getModifiableModel();
                for (ProjectDescriptor descriptor : myProjectDescriptors.values()) {
                    for (final ModuleDescriptor moduleDescriptor : descriptor.getModules()) {
                        final Module module;
                        if (moduleDescriptor.isReuseExistingElement()) {
                            final ExistingModuleLoader moduleLoader =
                                    ExistingModuleLoader.setUpLoader(FileUtil.toSystemIndependentName(moduleDescriptor.computeModuleFilePath()));
                            module = moduleLoader.createModule(moduleModel);
                        }
                        else {
                            module = createModule(descriptor, moduleDescriptor, projectLibs, moduleModel);
                        }
                        result.add(module);
                        descriptorToModuleMap.put(moduleDescriptor, module);
                    }
                }

                if (!fromProjectStructure) {
                    moduleModel.commit();
                }
            }
            finally {
                token.finish();
            }
        }
        catch (Exception e) {
            LOG.info(e);
            Messages.showErrorDialog(IdeBundle.message("error.adding.module.to.project", e.getMessage()), IdeBundle.message("title.add.module"));
        }

        // setup dependencies between modules
        try {
            AccessToken token = WriteAction.start();
            try {
                for (ProjectDescriptor data : myProjectDescriptors.values()) {
                    for (final ModuleDescriptor descriptor : data.getModules()) {
                        final Module module = descriptorToModuleMap.get(descriptor);
                        if (module == null) {
                            continue;
                        }
                        final Set<ModuleDescriptor> deps = descriptor.getDependencies();
                        if (deps.size() == 0) {
                            continue;
                        }
                        final ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
                        for (ModuleDescriptor dependentDescriptor : deps) {
                            final Module dependentModule = descriptorToModuleMap.get(dependentDescriptor);
                            if (dependentModule != null) {
                                rootModel.addModuleOrderEntry(dependentModule);
                            }
                        }
                        rootModel.commit();
                    }
                }
            }
            finally {
                token.finish();
            }
        }
        catch (Exception e) {
            LOG.info(e);
            Messages.showErrorDialog(IdeBundle.message("error.adding.module.to.project", e.getMessage()), IdeBundle.message("title.add.module"));
        }

//        AccessToken token = WriteAction.start();
//        try {
//            ModulesProvider updatedModulesProvider = fromProjectStructure ? modulesProvider : new DefaultModulesProvider(project);
//            for (ProjectFromSourcesBuilderImpl.ProjectConfigurationUpdater updater : myUpdaters) {
//                updater.updateProject(project, modelsProvider, updatedModulesProvider);
//            }
//        }
//        finally {
//            token.finish();
//        }


        return result;
    }

    private static Module createModule(ProjectDescriptor projectDescriptor, final ModuleDescriptor descriptor,
                                       final Map<LibraryDescriptor, Library> projectLibs, final ModifiableModuleModel moduleModel)
            throws InvalidDataException, IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {

        final String moduleFilePath = descriptor.computeModuleFilePath();
        ModuleBuilder.deleteModuleFile(moduleFilePath);

        final Module module = moduleModel.newModule(moduleFilePath, descriptor.getModuleType().getId());
        final ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
        setupRootModel(projectDescriptor, descriptor, modifiableModel, projectLibs);
        descriptor.updateModuleConfiguration(module, modifiableModel);
        modifiableModel.commit();
        return module;
    }

    private static void setupRootModel(ProjectDescriptor projectDescriptor, final ModuleDescriptor descriptor,
                                       final ModifiableRootModel rootModel, final Map<LibraryDescriptor, Library> projectLibs) {
        final CompilerModuleExtension compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExtension.setExcludeOutput(true);
        rootModel.inheritSdk();

        final Set<File> contentRoots = descriptor.getContentRoots();
        for (File contentRoot : contentRoots) {
            final LocalFileSystem lfs = LocalFileSystem.getInstance();
            VirtualFile moduleContentRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(contentRoot.getPath()));
            if (moduleContentRoot != null) {
                final ContentEntry contentEntry = rootModel.addContentEntry(moduleContentRoot);
                final Collection<DetectedSourceRoot> sourceRoots = descriptor.getSourceRoots(contentRoot);
                for (DetectedSourceRoot srcRoot : sourceRoots) {
                    final String srcpath = FileUtil.toSystemIndependentName(srcRoot.getDirectory().getPath());
                    final VirtualFile sourceRoot = lfs.refreshAndFindFileByPath(srcpath);
                    if (sourceRoot != null) {
                        contentEntry.addSourceFolder(sourceRoot, shouldBeTestRoot(srcRoot.getDirectory()), getPackagePrefix(srcRoot));
                    }
                }
            }
        }
    }


//        final ModuleInsight myInsight = new JavaModuleInsight(null, new HashSet<>(), new HashSet<>());
//        final ProjectBuilder projectBuilder = new ProjectFromSourcesBuilderImpl(wizardConext, null);

    //TODO separate to own method about creating .idea
//        try {
//            File projectDir = new File(path).getParentFile();
//            //LOG.assertTrue(projectDir != null, "Cannot create project in '" + path + "': no parent file exists");
//            FileUtil.ensureExists(projectDir);
//
//            final File ideaDir = new File(path, Project.DIRECTORY_STORE_FOLDER);
//            FileUtil.ensureExists(ideaDir);
//
//        } catch (Exception ex) {
//            System.out.println("virhe " + ex.toString());
//        }

    //TODO separate to method about setting/getting jdk
//
//        //seems like it returns biggest version that is registered to intellij
//        Sdk jdk = ProjectJdkTable.getInstance().findMostRecentSdkOfType(JavaSdk.getInstance());
//
//
//        //Sets jdk
//        try {
//            AccessToken token = WriteAction.start();
//            try {
//        NewProjectUtil.applyJdkToProject(newProject, jdk);
//                CommandProcessor.getInstance().executeCommand(newProject, () -> ApplicationManager.getApplication().runWriteAction(() -> applyJdkToProject(newProject, jdk)), null, null);
//
//                projectRootManager.setProjectSdk(jdk);
//            }
//            finally {
//                token.finish();
//            }
//        }
//        catch (Exception e) {
//            LOG.info(e);
//        }

    //TODO setting outpath in own method
//        String outpath = path + "/out";
//        String canonicalPath = outpath;
//        try {
//            canonicalPath = FileUtil.resolveShortWindowsName(outpath);
//        } catch (IOException e) {
//            //file doesn't exist
//        }
//        canonicalPath = FileUtil.toSystemIndependentName(canonicalPath);
//        CompilerProjectExtension.getInstance(newProject).setCompilerOutputUrl(VfsUtilCore.pathToUrl(canonicalPath));

    //TODO Setup module
//        try {
//            AccessToken token = WriteAction.start();
//            try {
//        final JavaModuleBuilder modulebuilder = new JavaModuleBuilder();
//        Collection<JavaModuleSourceRoot> sourceRoots = JavaSourceRootDetectionUtil.suggestRoots(new File(path));
//        //ModuleDescriptor moduleDescriptor = new ModuleDescriptor(new File(path), moduletype, sourceRoots);
//        String modulePath = path;
//        final ModifiableModuleModel model =
//                ModuleManager.getInstance(newProject).getModifiableModel();
//        modulebuilder.setName(modulePath.toString());
//        modulebuilder.setModuleFilePath(modulePath.toString());
//        modulebuilder.commit(newProject);
//
//
//        final Module module = ProjectRootManager.getInstance(newProject)
//                .getFileIndex().getModuleForFile(ProjectRootManager
//                        .getInstance(newProject).getContentRootsFromAllModules()[0]);
//        final ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
//        //setupRootModel(projectDescriptor, descriptor, modifiableModel, projectLibs);
//        //descriptor.updateModuleConfiguration(module, modifiableModel);
//        //modifiableModel.commit();
//
//
//        final CompilerModuleExtension compilerModuleExtension = rootModel.getModuleExtension(CompilerModuleExtension.class);
//        compilerModuleExtension.setExcludeOutput(true);
//        rootModel.inheritSdk();
//        final LocalFileSystem lfs = LocalFileSystem.getInstance();
//
//        for (DetectedSourceRoot srcRoot : sourceRoots) {
//            final String srcpath = FileUtil.toSystemIndependentName(srcRoot.getDirectory().getPath());
//            File contentRoot = new File(srcpath);
//            VirtualFile moduleContentRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(contentRoot.getPath()));
//            ContentEntry contentEntry = null;
//            if (moduleContentRoot == null) {
//                contentEntry = rootModel.addContentEntry(moduleContentRoot);
//            }
//            final VirtualFile sourceRoot = lfs.refreshAndFindFileByPath(srcpath);
//            if (sourceRoot != null && contentEntry != null) {
//                contentEntry.addSourceFolder(sourceRoot, shouldBeTestRoot(srcRoot.getDirectory()), getPackagePrefix(srcRoot));
//            }
//        }
//        compilerModuleExtension.inheritCompilerOutputPath(true);
//
//            }
//            finally {
//                token.finish();
//            }
//        }
//        catch (Exception e) {
//            LOG.info(e);
//        }



//        final List<DetectedSourceRoot> sourceRoots = new ArrayList<>();
//        for (ProjectStructureDetector detector : ProjectStructureDetector.EP_NAME.getExtensions()) {
//            for (DetectedProjectRoot root : myBuilder.getProjectRoots(detector)) {
//                if (myInsight.isApplicableRoot(root)) {
//                    sourceRoots.add((DetectedSourceRoot)root);
//                }
//            }
//        }
//        if (!ApplicationManager.getApplication().isUnitTestMode()) {
//            newProject.save();
//        }
    private static boolean shouldBeTestRoot(final File srcRoot) {
        if (isTestRootName(srcRoot.getName())) {
            return true;
        }
        final File parentFile = srcRoot.getParentFile();
        return parentFile != null && isTestRootName(parentFile.getName());
    }

    private static boolean isTestRootName(final String name) {
        return "test".equalsIgnoreCase(name) ||
                "tests".equalsIgnoreCase(name) ||
                "testSource".equalsIgnoreCase(name) ||
                "testSources".equalsIgnoreCase(name) ||
                "testSrc".equalsIgnoreCase(name) ||
                "tst".equalsIgnoreCase(name);
    }

}

