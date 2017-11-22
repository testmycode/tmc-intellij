package fi.helsinki.cs.tmc.intellij.services.login;

import com.google.common.base.Optional;
import com.intellij.openapi.components.ServiceManager;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.core.exceptions.AuthenticationFailedException;
import fi.helsinki.cs.tmc.core.exceptions.TmcCoreException;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.errors.ErrorMessageService;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class LoginManager {

    private IOException connectionException;
    private AuthenticationFailedException authenticationException;

    public boolean login(String password) {
        ErrorMessageService errorMessageService = new ErrorMessageService();

        try {
            TmcCoreHolder.get().authenticate(ProgressObserver.NULL_OBSERVER, password).call();
            return true;
        } catch (Exception e) {
            if (e instanceof IOException) {
                connectionException = (IOException)e;
                if (e instanceof UnknownHostException) {
                    errorMessageService.showHumanReadableErrorMessage((TmcCoreException)e, true);
                } else if (e instanceof FileNotFoundException) {
                    errorMessageService.showErrorMessage(e, "Server address is incorrect.", true);
                }
            }
            if (e instanceof AuthenticationFailedException) {
                authenticationException = (AuthenticationFailedException) e;
                errorMessageService.showErrorMessage(e, "Username or password is incorrect", true);
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
        final PersistentTmcSettings saveSettings =
                ServiceManager.getService(PersistentTmcSettings.class);
        final SettingsTmc settings = ServiceManager.getService(PersistentTmcSettings.class).getSettingsTmc();

        settings.setToken(Optional.absent());
        settings.setOauthCredentials(Optional.absent());
        settings.setUsername("");
        settings.setPassword(Optional.absent());
//        settings.setOrganization(Optional.absent());
//        settings.setCourse(null);

        saveSettings.setSettingsTmc(settings);
    }
}
