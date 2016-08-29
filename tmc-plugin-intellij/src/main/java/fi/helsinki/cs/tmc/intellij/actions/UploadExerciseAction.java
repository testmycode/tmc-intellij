package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.CoreProgressObserver;
import fi.helsinki.cs.tmc.intellij.services.CheckForExistingExercises;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ExerciseUploadingService;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.ProgressWindowMaker;
import fi.helsinki.cs.tmc.intellij.services.TestRunningService;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;
import fi.helsinki.cs.tmc.intellij.ui.submissionresult.SubmissionResultHandler;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uploads the currently active project to TMC Server
 * Defined in plugin.xml on the line
 * &lt;action id="Upload Exercise"
 *   class="fi.helsinki.cs.tmc.intellij.actions.UploadExerciseAction"&gt;
 * Uses CourseAndExerciseManager to update the view after upload,
 * SubmissionResultHandler displays the returned results
 */
public class UploadExerciseAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(UploadExerciseAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.info("Performing UploadExerciseAction. @UploadExerciseAction");
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        new ButtonInputListener().receiveSubmit();

        ProgressWindow window = ProgressWindowMaker.make(
                "Uploading exercise, this may take several minutes",
                project, true, true, true);
        CoreProgressObserver observer = new CoreProgressObserver(window);

        new ExerciseUploadingService().startUploadExercise(project,
                TmcCoreHolder.get(), new ObjectFinder(),
                new CheckForExistingExercises(), new SubmissionResultHandler(),
                TmcSettingsManager.get(),
                new CourseAndExerciseManager(),
                new ThreadingService(),
                new TestRunningService(),
                observer,
                window);
    }

}
