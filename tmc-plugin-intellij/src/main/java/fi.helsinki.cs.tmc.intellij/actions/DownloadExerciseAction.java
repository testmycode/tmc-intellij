package fi.helsinki.cs.tmc.intellij.actions;


import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.services.CheckForExistingExercises;
import fi.helsinki.cs.tmc.intellij.services.ExerciseDownloadingService;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defined in plugin.xml on line
 *  &lt;action id="Download Exercises"
 *    class="fi.helsinki.cs.tmc.intellij.actions.DownloadExerciseAction"&gt;
 * in group actions
 *
 * <p>
 * Downloads exercises from the course selected in settings,
 * uses CheckForExistingExercises to check already downloaded ones,
 * updates exercise lists with CourseAndExeriseManager
 * </p>
 */
public class DownloadExerciseAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(DownloadExerciseAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        logger.info("Performing DownloadExerciseAction.");
        try {
            ExerciseDownloadingService.startDownloadExercise(TmcCoreHolder.get(),
                    TmcSettingsManager.get(),
                    new CheckForExistingExercises(),
                    new ProjectOpener());
        } catch (Exception e) {
            logger.warn("Downloading failed.", e);
            Messages.showMessageDialog(project,
                    "Downloading failed \n"
                            + "Are your account details correct?\n"
                            + e.getMessage(), "Result", Messages.getErrorIcon());
        }
    }
}