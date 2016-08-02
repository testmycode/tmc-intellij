package fi.helsinki.cs.tmc.intellij.services;


import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;


public class StartupErrorMessageService {

    private String notifyAboutUsernamePasswordAndServerAddress(TmcCoreException exception) {
        return errorCode(exception)
                + "Seems like you don't have your TMC settings initialized. \n"
                + "Set up your username, password and TMC server address.";
    }

    private String notifyAboutInternetConnection(TmcCoreException exception) {
        return errorCode(exception) + "Check your internet connection.";
    }

    private String notifyAboutEmptyServerAddress(TmcCoreException exception) {
        return errorCode(exception)
                + "You need to set up TMC server address "
                + "to be able to download and submit exercises.";
    }

    private String errorCode(TmcCoreException exception) {
        return exception.getCause().getMessage() + ". \n";
    }

    public static final NotificationGroup TMC_NOTIFICATION =
            new NotificationGroup("TMC Error Messages",
                    NotificationDisplayType.STICKY_BALLOON, true);

    private void initializeNotification(String str) {
        Project projects = new ObjectFinder().findCurrentProject();
        Notification notification = TMC_NOTIFICATION
                .createNotification(str,
                        NotificationType.WARNING);
        Notifications.Bus.notify(notification, projects);
    }

    public void showMessage(final TmcCoreException exception) {

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                String str = exception.getCause().getMessage();

                if (str.contains("Download failed: tmc.mooc.fi: unknown error")) {
                    initializeNotification(notifyAboutInternetConnection(exception));
                } else if (!TmcSettingsManager.get().userDataExists()) {
                    initializeNotification(notifyAboutUsernamePasswordAndServerAddress(exception));
                } else if (TmcSettingsManager.get().getServerAddress().isEmpty()) {
                    initializeNotification(notifyAboutEmptyServerAddress(exception));
                } else {
                    errorCode(exception);
                }
            }
        });
    }
}
/*
        ToolBar toolBar = WindowManager.getInstance()
                .getToolBar(DataKeys.PROJECT.getData(actionEvent.getDataContext()));

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(htmlText, messageType, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(toolBar.getComponent()),
                        Balloon.Position.atRight);
*/
