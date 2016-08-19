package fi.helsinki.cs.tmc.intellij.importExercise;

import com.intellij.ide.util.importProject.JavaModuleInsight;
import com.intellij.ide.util.importProject.LibraryDescriptor;
import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ModuleInsight;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.importSources.JavaModuleSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.JavaSourceRootDetectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;


public class JavaProjectDescriptor {
    private static final Logger logger =
            LoggerFactory.getLogger(JavaProjectDescriptor.class);

    /**
     * Creates ProjectDescriptor so project can be build from it's info.
     * @param path
     * @param ignoredNames
     * @return
     */
    public static ProjectDescriptor create(
            String path, Set<String> ignoredNames) {
        logger.info("Starting to create Project Descriptor in JavaProjectDescriptor.");
        File contentFile = new File(path);
        ProjectDescriptor projectDescriptor = new ProjectDescriptor();
        List<ModuleDescriptor> moduleList = new ArrayList<>();
        List<LibraryDescriptor> libraryList = new ArrayList<>();
        projectDescriptor.setModules(moduleList);
        projectDescriptor.setLibraries(libraryList);

        Collection<JavaModuleSourceRoot> suggestedRoots =
                JavaSourceRootDetectionUtil.suggestRoots(contentFile);

        List<JavaModuleSourceRoot> rootList = new ArrayList<>();
        rootList.addAll(suggestedRoots);

        ModuleInsight insight = new JavaModuleInsight(
                null, new HashSet<>(), new HashSet<>());
        List<File> entryPointRoots = new ArrayList<>();
        entryPointRoots.add(contentFile);
        insight.setRoots(entryPointRoots, rootList, ignoredNames);

        insight.scanLibraries();
        insight.scanModules();

            libraryList.addAll(insight.getSuggestedLibraries());


            moduleList.addAll(insight.getSuggestedModules());
        logger.info("Ending to create Project Descriptor in JavaProjectDescriptor.");
        return projectDescriptor;
    }
}
