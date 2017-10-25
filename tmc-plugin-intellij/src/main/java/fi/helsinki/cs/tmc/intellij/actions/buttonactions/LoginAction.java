package fi.helsinki.cs.tmc.intellij.actions.buttonactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import fi.helsinki.cs.tmc.core.domain.ProgressObserver;
import fi.helsinki.cs.tmc.intellij.holders.TmcCoreHolder;
import fi.helsinki.cs.tmc.intellij.io.SettingsTmc;
import fi.helsinki.cs.tmc.intellij.services.persistence.PersistentTmcSettings;

/** Created by vikangas on 23/10/17. */
public class LoginAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        authenticate();
    }

    public boolean authenticate() {
        final SettingsTmc settingsTmc = ServiceManager.getService(PersistentTmcSettings.class)
                .getSettingsTmc();

        try {
            System.out.println("authenticating");
            TmcCoreHolder.get().authenticate(ProgressObserver.NULL_OBSERVER, settingsTmc.getPassword().get()).call();
            return true;
        } catch (Exception e) {
            System.out.println("authenticating didn't work fuuuuuuk");

            return false;
        }
    }
}
