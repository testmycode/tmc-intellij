package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.notification.NotificationType;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;

public class CheckForOneDrive {

    public static void run() {
        SettingsTmc settings = TmcSettingsManager.get();

        if (!settings.getTmcProjectDirectory().toAbsolutePath().toString().toLowerCase().contains("onedrive")) {
            return;
        }

        ErrorMessageService errorMessageService = new ErrorMessageService();
        String message = "OneDrive does not work with the TestMyCode plugin for IntelliJ\n\n" +
            "Many students have had a problem when they have their IntelliJ project folder in OneDrive, which results in submissions failing on the server. \n" +
            "The problem can be solved by moving the project folder out of OneDrive as follows:\n\n" +
            "- Copy or move the \"IdeaProjects\" folder to some path that does not contain the \"OneDrive\" folder.\n" +
            "- Change the IntelliJ project folder to the path you selected in the previous step by selecting \"TMC\" -> \"Settings\".\n" +
            "- Now you can open the moved exercises by selecting \"File\" -> \"Open...\" and selecting the exercises from the new folder.";
        errorMessageService.showErrorMessagePopup(message);
    }
}
