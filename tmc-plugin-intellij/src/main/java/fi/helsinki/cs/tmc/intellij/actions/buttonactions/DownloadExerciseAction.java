package fi.helsinki.cs.tmc.intellij.actions.buttonactions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.ProgressWindowMaker;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.services.exercises.CheckForExistingExercises;
import fi.helsinki.cs.tmc.intellij.services.exercises.ExerciseDownloadingService;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defined in plugin.xml on line &lt;action id="Download Exercises"
 * class="fi.helsinki.cs.tmc.intellij.actions.buttonactions.DownloadExerciseAction"&gt; in group
 * actions
 *
 * <p>Downloads exercises from the course selected in settings, uses CheckForExistingExercises to
 * check already downloaded ones, updates exercise lists with CourseAndExerciseManager
 */
public class DownloadExerciseAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(DownloadExerciseAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        new ButtonInputListener().receiveDownloadExercise();
        downloadExercises(project, false);
    }

    public void downloadExercises(Project project, boolean downloadAll) {
        logger.info("Performing DownloadExerciseAction. @DownloadExerciseAction");
        try {
            startDownloadExercise(project, downloadAll);
        } catch (Exception exception) {
            logger.warn("Downloading failed. @DownloadExerciseAction", exception);
            Messages.showMessageDialog(
                    project,
                    "Downloading failed \n"
                            + "Are your account details correct?\n"
                            + exception.getMessage(),
                    "Result",
                    Messages.getErrorIcon());
        }
    }

    private void startDownloadExercise(Project project, boolean downloadAll) throws Exception {
        ProgressWindow window =
                ProgressWindowMaker.make(
                        "Downloading exercises, this may take several minutes",
                        project,
                        true,
                        true,
                        true);
        ExerciseDownloadingService
                .startDownloadExercise(
                        TmcCoreHolder.get(),
                        TmcSettingsManager.get(),
                        new CheckForExistingExercises(),
                        new ObjectFinder(),
                        new ThreadingService(),
                        project,
                        downloadAll,
                        window);
    }
}
