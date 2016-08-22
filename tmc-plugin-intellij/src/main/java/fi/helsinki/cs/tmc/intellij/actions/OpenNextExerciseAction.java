package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.NextExerciseFetcher;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenNextExerciseAction extends com.intellij.openapi.actionSystem.AnAction {

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
        if (settings.getCourse() != null && (project == null || project.getBasePath() == null
                || !PathResolver.getCourseName(project.getBasePath())
                .equals(settings.getCourseName()))) {
            CourseAndExerciseManager manager = new CourseAndExerciseManager();
            NextExerciseFetcher.openFirst(manager.getExercises(settings.getCourseName()));
        } else {
            String path = project.getBasePath();
            NextExerciseFetcher fetcher = new NextExerciseFetcher(PathResolver
                    .getCourseName(path), PathResolver.getExercise(path), project);
            fetcher.tryToOpenNext();
        }
    }
}
