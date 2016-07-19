package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.Messages;
import fi.helsinki.cs.tmc.intellij.services.SaveSettingsService;
import org.jetbrains.annotations.NotNull;

public class StartupEvent implements StartupActivity{

    @Override
    public void runActivity(@NotNull Project project) {

//        final SaveSettingsService saveSettings = ServiceManager.getService(SaveSettingsService.class);

//
//        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
//        String psw= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
//        if (!txt.isEmpty()){
//            saveSettings.setuserName(txt);
//            saveSettings.setpassword(psw);
//        }
//        Messages.showMessageDialog(project, "Current user: " + saveSettings.getuserName() + " "  + saveSettings.getpassword(), "Information", Messages.getInformationIcon());

    }
}
