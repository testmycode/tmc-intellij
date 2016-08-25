package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.TestRunningService;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTestsAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(RunTestsAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.info("Run tests action performed. @RunTestsAction");
        String[] courseExercise = PathResolver.getCourseAndExerciseName(anActionEvent.getProject());

        new ButtonInputListener().receiveTestRun();

        new TestRunningService().runTests(
                new CourseAndExerciseManager().getExercise(getCourseName(courseExercise),
                        getExerciseName(courseExercise)),
                anActionEvent.getProject(),
                new ThreadingService(),
                new ObjectFinder());
    }

    private String getCourseName(String[] courseExercise) {
        return courseExercise[courseExercise.length - 2];
    }

    private String getExerciseName(String[] courseExercise) {
        return courseExercise[courseExercise.length - 1];

    }
}
