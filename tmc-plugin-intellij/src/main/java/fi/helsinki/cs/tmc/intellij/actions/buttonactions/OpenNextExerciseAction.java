package fi.helsinki.cs.tmc.intellij.actions.buttonactions;

import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.exercises.NextExerciseFetcher;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenNextExerciseAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(OpenNextExerciseAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        openExercise(anActionEvent.getProject());
    }

    /**
     * Opens next exercise from the chosen course in settings.
     * If current project is from that course the next exercise is chosen
     * using the open exercise.
     */
    public void openExercise(Project project) {
        logger.info("Opening next exercise");
        SettingsTmc settings = TmcSettingsManager.get();

        NextExerciseFetcher.openNext(project, settings);
    }
}
