package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.NextExerciseFetcher;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;

public class OpenNextExerciseAction extends com.intellij.openapi.actionSystem.AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        openExercise(anActionEvent.getProject());
    }

    public void openExercise(Project project) {
        SettingsTmc settings = TmcSettingsManager.get();
        if (settings.getCourse() != null && (project == null || project.getBasePath() == null
                || !PathResolver.getCourseName(project.getBasePath()).equals(settings.getCourseName()))) {
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
