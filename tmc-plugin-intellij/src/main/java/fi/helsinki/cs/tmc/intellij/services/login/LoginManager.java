package fi.helsinki.cs.tmc.intellij.services.login;

import com.google.common.base.Optional;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.AuthenticationFailedException;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class LoginManager {

    private IOException connectionException;
    private AuthenticationFailedException authenticationException;

    private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

    public boolean login(String password) {
        ErrorMessageService errorMessageService = new ErrorMessageService();

        try {
            logger.info("Authenticating user. @LoginManager");

            TmcCoreHolder.get().authenticate(ProgressObserver.NULL_OBSERVER, password).call();
            return true;
        } catch (Exception e) {
            if (e instanceof IOException) {
                connectionException = (IOException) e;
                if (e instanceof UnknownHostException) {
                    errorMessageService.showErrorMessage(
                            e,
                            "Couldn't connect to the server. Please check your internet connection.",
                            true);
                } else if (e instanceof FileNotFoundException) {
                    if (TmcSettingsManager.get().getServerAddress().contains("tmc.mooc.fi/mooc")) {
                        Messages.showErrorDialog(
                                "The server https://tmc.mooc.fi/mooc is no longer supported by this client.\n"
                                        + "All the courses have been migrated to our main server https://tmc.mooc.fi.\n"
                                        + "If you'd like to do the migrated courses, you'll need to update your server address to\n"
                                        + "https://tmc.mooc.fi and create a new account there.\n"
                                        + "After that, choose the MOOC organization after logging in.\n\n"
                                        + "For more information, check the course materials on mooc.fi.",
                                "Error");
                    } else {
                        errorMessageService.showErrorMessage(
                                e,
                                "Server address is incorrect or the server is not supported.",
                                true);
                    }
                }
            } else if (e instanceof AuthenticationFailedException) {
                authenticationException = (AuthenticationFailedException) e;
                errorMessageService.showErrorMessage(e, "Username or password is incorrect", true);
            } else {
                Messages.showErrorDialog("Logging in failed! Try again.", "Error");
            }

            if (authenticationException != null) {
                try {
                    throw authenticationException;
                } catch (AuthenticationFailedException e1) {
                    e1.printStackTrace();
                }
            }
            if (connectionException != null) {
                try {
                    throw connectionException;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            return false;
        }
    }

    public void logout() {
        logger.info("Logging out user. @LoginManager");

        final PersistentTmcSettings saveSettings =
                ServiceManager.getService(PersistentTmcSettings.class);
        final SettingsTmc settings =
                ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();

        settings.setToken(Optional.absent());
        settings.setOauthCredentials(Optional.absent());
        settings.setUsername("");
        settings.setPassword(Optional.absent());

        saveSettings.setSettingsTmc(settings);
    }
}
