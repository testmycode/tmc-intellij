package fi.helsinki.cs.tmc.intellij.importexercise;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class JavaProjectDescriptor {
    private static final Logger logger = LoggerFactory.getLogger(JavaProjectDescriptor.class);

    /**
     * Creates ProjectDescriptor so project can be build from it's info.
     *
     * @param path project root dir
     * @param ignoredNames ie. compile output path
     * @return ProjectDescriptor
     */
    public static ProjectDescriptor create(String path, Set<String> ignoredNames) {
        logger.info("Starting to create Project Descriptor in JavaProjectDescriptor.");

        logger.info("Initializing ProjectDescriptor");
        File contentFile = new File(path);
        ProjectDescriptor projectDescriptor = new ProjectDescriptor();
        List<ModuleDescriptor> moduleList = new ArrayList<>();
        List<LibraryDescriptor> libraryList = new ArrayList<>();
        projectDescriptor.setModules(moduleList);
        projectDescriptor.setLibraries(libraryList);

        logger.info("Searching for source files");
        //test file is considered as source file at this point
        Collection<JavaModuleSourceRoot> suggestedRoots =
                JavaSourceRootDetectionUtil.suggestRoots(contentFile);

        List<JavaModuleSourceRoot> rootList = new ArrayList<>();
        rootList.addAll(suggestedRoots);

        //Module insight creates module and library descriptors collecting data as long as roots are listed.
        ModuleInsight insight = new JavaModuleInsight(null, new HashSet<>(), new HashSet<>());
        List<File> entryPointRoots = new ArrayList<>();
        entryPointRoots.add(contentFile);

        logger.info("Setting up ModuleInsight roots");
        insight.setRoots(entryPointRoots, rootList, ignoredNames);

        logger.info("ModuleInsight scanning for modules and libraries");
        insight.scanLibraries();
        insight.scanModules();

        logger.info("Adding moduleDescriptors and LibraryDescriptors to projectDescriptor");
        libraryList.addAll(insight.getSuggestedLibraries());

        moduleList.addAll(insight.getSuggestedModules());

        logger.info("Ending to create Project Descriptor in JavaProjectDescriptor.");
        return projectDescriptor;
    }
}
