package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadAllExercisesAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(DownloadAllExercisesAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.info("Starting to download all courses exercises. @DownloadAllExercisesAction");
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        new ButtonInputListener().receiveDownloadExercise();
        new DownloadExerciseAction().downloadExercises(project, true);
    }

}
