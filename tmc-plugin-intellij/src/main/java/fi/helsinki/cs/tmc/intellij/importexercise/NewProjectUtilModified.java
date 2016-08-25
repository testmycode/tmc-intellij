/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author max
 */

package fi.helsinki.cs.tmc.intellij.importexercise;

import static com.intellij.diff.tools.simple.ThreesideTextDiffViewerEx.LOG;
import static com.intellij.ide.impl.NewProjectUtil.applyJdkToProject;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectManagerImpl;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

 /*
 * this code is modified form intellij code. url below
 * original class: NewProjectUtil
 * https://github.com/JetBrains/intellij-community/blob/9132043620dd78fbff9e77e78d00efb8972613bc/java/idea-ui/src/com/intellij/ide/impl/NewProjectUtil.java
 */

public class NewProjectUtilModified {
    private static final Logger logger = LoggerFactory
            .getLogger(NewProjectUtilModified.class);

    /*
     * Handles importing exercises to intellij only using file root as source of info.
     * In original execution method is named "doImport(param.)"
     * @param path project root dir
     * @throws IOException
     */
    public static void importExercise(String path) throws IOException {
        logger.info("Started importing exercise.");

        final String projectFilePath = path;

        final Project newProject;

        try {
            logger.info("Creates .idea");
            File projectDir = new File(projectFilePath).getParentFile();
            LOG.assertTrue(projectDir != null, "Cannot create project in '"
                    + projectFilePath + "': no parent file exists");
            FileUtil.ensureExists(projectDir);

            final File ideaDir = new File(projectFilePath, Project.DIRECTORY_STORE_FOLDER);
            FileUtil.ensureExists(ideaDir);

            newProject = ProjectManagerImpl.getInstanceEx()
                    .newProject(path.toString(), path.toString(), true, false);

            logger.info("Setting JDK");
            final Sdk jdk = ProjectJdkTable.getInstance()
                    .findMostRecentSdkOfType(JavaSdk.getInstance());
            if (jdk != null) {
                CommandProcessor.getInstance().executeCommand(newProject, ()
                        -> ApplicationManager.getApplication().runWriteAction(()
                                -> applyJdkToProject(newProject, jdk)), null, null);
            }


            logger.info("Sets compile output path");
            final String compileOutput = StringUtil.endsWithChar(path, '/')
                    ? path + "out" : path + "/out";;
            CommandProcessor.getInstance().executeCommand(newProject, () ->
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        String canonicalPath = compileOutput;
                        try {
                            canonicalPath = FileUtil.resolveShortWindowsName(compileOutput);
                        } catch (IOException e) {
                            //file doesn't exist
                            logger.warn("File doesn't exist.", e);
                        }

                        canonicalPath = FileUtil.toSystemIndependentName(canonicalPath);
                        CompilerProjectExtension.getInstance(newProject)
                                .setCompilerOutputUrl(VfsUtilCore.pathToUrl(canonicalPath));
                    }), null, null);
            logger.info("Saving project created this far");

            // Saving changes seems to write things up.
            if (!ApplicationManager.getApplication().isUnitTestMode()) {
                newProject.save();
            }

            ProjectFromSourcesBuilderImplModified
                    .commit(newProject, path);


            logger.info("saving project after builder commit");
            // without save method nothing happens
            ProjectUtil.updateLastProjectLocation(projectFilePath);
            newProject.save();
            if (!ApplicationManager.getApplication().isUnitTestMode()) {
                newProject.save();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        logger.info("Exercise import progress is finished.");
    }
}
