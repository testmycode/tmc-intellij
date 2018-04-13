package fi.helsinki.cs.tmc.intellij.actions.buttonactions;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import fi.helsinki.cs.tmc.intellij.services.TestRunningService;
import fi.helsinki.cs.tmc.intellij.services.ThreadingService;
import fi.helsinki.cs.tmc.intellij.services.exercises.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.spyware.ButtonInputListener;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTestsAction extends AnAction {

    private static final Logger logger = LoggerFactory.getLogger(RunTestsAction.class);

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        logger.info("Run tests action performed. @RunTestsAction");
        String[] courseExercise = PathResolver.getCourseAndExerciseName(anActionEvent.getProject());
        Course course = new ObjectFinder().findCourse(getCourseName(courseExercise), "name");

        FileDocumentManager.getInstance().saveAllDocuments();

        new ButtonInputListener().receiveTestRun();

        new TestRunningService()
                .runTests(
                        new CourseAndExerciseManager()
                                .getExercise(
                                        course.getTitle(),
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
