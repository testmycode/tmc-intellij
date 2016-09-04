package fi.helsinki.cs.tmc.intellij.services.logging;

import com.intellij.openapi.application.PathManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PropertySetter {

    public void setLog4jProperties() {
        File file = new File(PathManager.getPluginsPath()
                + "/tmc-plugin-intellij/lib/tmc-plugin-intellij.jar");

        if (file.exists()) {
            setPluginLog();
        } else {
            setDevLog();
        }
    }

    private void setPluginLog() {
        String jarPath = PathManager.getPluginsPath()
                + "/tmc-plugin-intellij/lib/tmc-plugin-intellij.jar";
        try {
            JarFile jar = new JarFile(jarPath);
            JarEntry entry = jar.getJarEntry("log4j.properties");
            InputStream is = jar.getInputStream(entry);
            PropertyConfigurator.configure(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDevLog() {
        InputStream is = getClass().getResourceAsStream("/log4j.properties");
        PropertyConfigurator.configure(is);
    }
}
