package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import fi.helsinki.cs.tmc.core.TmcCore;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.domain.submission.SubmissionResult;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.ui.SubmissionResultPopup;

import java.util.List;

public class UploadExerciseAction extends AnAction{

    private TmcCore core = TmcCoreHolder.get();

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        uploadExercise(project);
    }

    public void uploadExercise(Project project){
        String path = project.getBasePath();
        System.out.println();
        String[] exerciseCourse = new String[2];
        if (path.contains("/")) {
            exerciseCourse = path.split("/");
        } else {
            String backslash = " \\ ";
            backslash.trim();
            exerciseCourse = path.split(backslash);
        }

        Course course = findCourseByName(exerciseCourse[exerciseCourse.length - 2]);
        Exercise exercise = findExerciseByName(course, exerciseCourse[exerciseCourse.length - 1]);

        try {
            SubmissionResult result = core.submit(ProgressObserver.NULL_OBSERVER, exercise).call();
            Messages.showMessageDialog(project, result.toString(), "Result", Messages.getQuestionIcon());
        } catch (Exception e) {
            e.printStackTrace(); 
        }

    }

    private Exercise findExerciseByName(Course course, String exerciseName) {
        System.out.println(course.getExercises());
        List<Exercise> exercises = course.getExercises();
        for(Exercise exercise: exercises){
            if(exercise.getName().equals(exerciseName)){
                return exercise;
            }
        }
        return null;
    }

    private Course findCourseByName(String courseName) {
        List<Course> courses = null;
        try {
            courses = core.listCourses(ProgressObserver.NULL_OBSERVER).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                try {
                    return core.getCourseDetails(ProgressObserver.NULL_OBSERVER, course).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
