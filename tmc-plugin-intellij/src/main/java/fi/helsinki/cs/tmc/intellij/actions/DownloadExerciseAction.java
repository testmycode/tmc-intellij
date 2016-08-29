package fi.helsinki.cs.tmc.intellij.actions;


import com.intellij.openapi.progress.util.ProgressWindow;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.*;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

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
        new ButtonInputListener().receiveDownloadExercise();
        downloadExercises(project);
    }


    public void downloadExercises(Project project) {
        logger.info("Performing DownloadExerciseAction. @DownloadExerciseAction");
        try {
            ProgressWindow window = ProgressWindowMaker.make(
                    "Downloading exercises, this may take several minutes", project, true, true, false);
            CoreProgressObserver observer = new CoreProgressObserver(window);
            new ExerciseDownloadingService().startDownloadExercise(TmcCoreHolder.get(),
                    TmcSettingsManager.get(),
                    new CheckForExistingExercises(),
                    new ObjectFinder(),
                    new ThreadingService(),
                    project,
                    window,
                    observer);


        } catch (Exception exception) {
            logger.warn("Downloading failed. @DownloadExerciseAction", exception);
            Messages.showMessageDialog(project,
                    "Downloading failed \n"
                            + "Are your account details correct?\n"
                            + exception.getMessage(), "Result", Messages.getErrorIcon());
        }
    }
}