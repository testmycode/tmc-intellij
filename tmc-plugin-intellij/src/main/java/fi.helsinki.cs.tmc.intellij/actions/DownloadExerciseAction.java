package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.CheckForExistingExercises;

import java.util.ArrayList;
import java.util.List;

public class DownloadExerciseAction extends AnAction {

    private Exercise exercise;
    private TmcCore core;
    private List<Exercise> list;

    public DownloadExerciseAction() {
        exercise = new Exercise();
        core = TmcCoreHolder.get();
        list = new ArrayList<Exercise>();
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            downloadExerciseAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Exercise> downloadExerciseAction() throws Exception {

        List<Exercise> exercises = TmcCoreHolder.get().getCourseDetails(ProgressObserver.NULL_OBSERVER, TmcSettingsManager.get().getCourse()).call().getExercises();

        exercises = CheckForExistingExercises.clean(exercises);
        core.downloadOrUpdateExercises(ProgressObserver.NULL_OBSERVER, exercises).call();
        return exercises;
    }


}