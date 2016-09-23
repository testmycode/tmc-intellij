package fi.helsinki.cs.tmc.intellij.services.errors;


import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

import com.intellij.openapi.application.ApplicationManager;

import com.intellij.openapi.project.Project;

import com.intellij.openapi.ui.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pops up user friendly warnings and specified error messages. For example pops up an error message
 * on startup if there is no internet connection or TMC user settings (username, password or server
 * address) are incorrect.
 */
public class ErrorMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorMessageService.class);

    private String notifyAboutCourseServerAddressAndInternet() {
        logger.info(
                "Picking up Error message from notifyAboutCourseServerAddressAndInternet. "
                        + "@ErrorMessageService");
        return "Failed to download courses\n"
                + "Check that you have the correct course and server address\n"
                + "and you are connected to Internet";
    }

    /**
     * Error message, if TMC username or password are not initialized.
     *
     * @param exception The cause of an error.
     * @return String. Error message that will be shown to the user.
     */
    private String notifyAboutUsernamePasswordAndServerAddress(TmcCoreException exception) {
        logger.info(
                "Picking up Error message from notifyAboutUsernamePasswordAndServerAddress. "
                        + "@ErrorMessageService");
        return errorCode(exception)
                + "Seems like you don't have your TMC settings initialized. \n"
                + "Set up your username, password and TMC server address.";
    }

    /**
     * Error message, if TMC server address has not been initialized.
     *
     * @param exception The cause of an error.
     * @return String. Error message that will be shown to the user.
     */
    private String notifyAboutEmptyServerAddress(TmcCoreException exception) {
        logger.info(
                "Picking up Error message from notifyAboutEmptyServerAddress. "
                        + "@ErrorMessageService");
        return errorCode(exception)
                + "You need to set up TMC server address "
                + "to be able to download and submit exercises.";
    }

    private String notifyAboutFailedSubmissionAttempt(TmcCoreException exception) {
        logger.info(
                "Picking up Error message from notifyAboutFailedSubmissionAttempt. "
                        + "@ErrorMessageService");
        return "Failed to establish connection to the server" + "\n Check your Internet connection";
    }

    /**
     * Error message, if TMC username or password is incorrect.
     *
     * @param exception The cause of an error.
     * @return String. Error message that will be shown to the user.
     */
    private String notifyAboutIncorrectUsernameOrPassword(TmcCoreException exception) {
        logger.info(
                "Error message from notifyAboutIncorrectUsernameOrPassword. "
                        + "@ErrorMessageService");
        return errorCode(exception) + "TMC Username or Password incorrect.";
    }

    /**
     * Error message, prints out the cause of the current exception.
     *
     * @param exception The cause of an error.
     * @return String. Error message that will be shown to the user.
     */
    private String errorCode(TmcCoreException exception) {
        logger.info(
                "Adding Error cause to the Error message shown to the user. "
                        + "@ErrorMessageService");
        return exception.getCause().getMessage() + ". \n";
    }

    /**
     * Error message, prints out the cause of the current exception.
     *
     * @param exception The cause of an error.
     * @return String. Error message that will be shown to the user.
     */
    private String errorCode(Exception exception, String str) {
        logger.info(
                "Adding exception to the Error message shown to the user. "
                        + "@ErrorMessageService");
        return exception + ". \n" + str;
    }

    /**
     * Creates a new notification group TMC_NOTIFICATION for TMC notifications to the notification
     * board.
     */
    public static final NotificationGroup TMC_NOTIFICATION =
            new NotificationGroup(
                    "TMC Error Messages", NotificationDisplayType.STICKY_BALLOON, true);

    /**
     * Generates a balloon notification or a popup message.
     *
     * @param str Notification message.
     * @param type The type of notification to be shown.
     * @param bool If the error message will be a popup or not.
     */
    private void initializeNotification(final String str, NotificationType type, boolean bool) {
        logger.info(
                "Working on what kind of error notification will be "
                        + "shown to the user. @ErrorMessageService");
        final Project projects = new ObjectFinder().findCurrentProject();
        if (bool) {
            logger.info(
                    "Interrupting Error message popup. Location middle of the screen. "
                            + "@ErrorMessageService");
            Messages.showMessageDialog(projects, str, "", Messages.getErrorIcon());
        } else {
            logger.info(
                    "User friendly STICKY_BALLOON notification."
                            + " Location right corner. @ErrorMessageService");
            Notification notification = TMC_NOTIFICATION.createNotification(str, type);
            Notifications.Bus.notify(notification, projects);
        }
    }

    /**
     * Selects the error message method to be called.
     *
     * @param exception The cause of an error.
     * @param bool If the error message will be a popup or not.
     */
    private void selectMessage(TmcCoreException exception, boolean bool) {
        logger.info("Selecting the error message method to be called. @ErrorMessageService");
        String str = exception.getCause().getMessage();
        NotificationType type = NotificationType.WARNING;
        if (str.contains("Download failed") || str.contains("404") || str.contains("500")) {
            initializeNotification(notifyAboutCourseServerAddressAndInternet(), type, bool);
        } else if (!TmcSettingsManager.get().userDataExists()) {
            initializeNotification(
                    notifyAboutUsernamePasswordAndServerAddress(exception), type, bool);
        } else if (str.contains("401")) {
            initializeNotification(
                    notifyAboutIncorrectUsernameOrPassword(exception),
                    NotificationType.ERROR,
                    bool);
        } else if (exception.getMessage().contains("Failed to fetch courses from the server")
                || exception.getMessage().contains("Failed to compress project")) {
            initializeNotification(notifyAboutFailedSubmissionAttempt(exception), type, bool);
        } else if (TmcSettingsManager.get().getServerAddress().isEmpty()) {
            initializeNotification(notifyAboutEmptyServerAddress(exception), type, bool);
        } else {
            initializeNotification(errorCode(exception), NotificationType.ERROR, bool);
            exception.printStackTrace();
        }
    }

    /**
     * Controls which error message will be shown to the user. If the parameter bool is true, the
     * message will be shown as a popup. If not, then it will be shown at the side.
     *
     * @param exception The cause of an error.
     * @param bool if the error message will be a pop up or not.
     */
    public void showMessage(final TmcCoreException exception, final boolean bool) {
        logger.info("Starting to handle TmcCoreException." + " {} @ErrorMessageService", exception);
        if (bool) {
            logger.info("Redirecting to selectMessage. @ErrorMessageService");
            selectMessage(exception, bool);
        } else {
            logger.info("Executing on pooled thread. @ErrorMessageService");
            ApplicationManager.getApplication()
                    .invokeLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    logger.info(
                                            "Redirecting to selectMessage . @ErrorMessageService");
                                    selectMessage(exception, bool);
                                }
                            });
        }
    }

    /**
     * Redirecting the error handling to another showMessage method with boolean parameter.
     *
     * @param exception The cause of an error.
     * @param errorMessage Error message.
     */
    public void showMessage(final Exception exception, final String errorMessage) {
        logger.info(
                "Redirecting Error message handling to showMessage(Exception, "
                        + "String, boolean), boolean as false. @ErrorMessageService");
        showMessage(exception, errorMessage, false);
    }

    /**
     * Controls which error message will be shown to the user.
     *
     * @param exception The cause of an error.
     * @param errorMessage Error message.
     */
    public void showMessage(
            final Exception exception, final String errorMessage, final boolean bool) {
        logger.info("Starting to handle Exception " + " {} . @ErrorMessageService", exception);
        ApplicationManager.getApplication()
                .invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                initializeNotification(
                                        errorCode(exception, errorMessage),
                                        NotificationType.ERROR,
                                        bool);
                                exception.printStackTrace();
                            }
                        });
    }

    public void downloadErrorMessage(Course course) {
        //final Project projects = new ObjectFinder().findCurrentProject();
        ApplicationManager.getApplication()
                .invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                Messages.showMessageDialog(
                                        "All exercises for "
                                                + course.toString()
                                                + " are up to date.",
                                        "All exercises are up to date.",
                                        Messages.getInformationIcon());
                            }
                        });
    }
}
