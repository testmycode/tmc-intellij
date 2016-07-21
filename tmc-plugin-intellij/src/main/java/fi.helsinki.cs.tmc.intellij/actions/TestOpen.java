package fi.helsinki.cs.tmc.intellij.actions;

import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.ProjectOpener;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;


/**
 * Created by samukaup on 20.7.2016.
 */
public class TestOpen extends AnAction{

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        //SettingsTmc settings = TmcSettingsManager.get();
        System.out.println(anActionEvent.getProject().getBasePath());
        ProjectOpener action = new ProjectOpener();
        try {
            //String str = settings.getProjectBasePath();
            action.openProject("/home/samukaup/aaa/tmc-intellij-test-course/arith_funcs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
