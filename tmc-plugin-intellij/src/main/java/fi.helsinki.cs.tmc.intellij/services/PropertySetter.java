package fi.helsinki.cs.tmc.intellij.services;

import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;

public class PropertySetter {

    public void setLog4jProperties() {
        InputStream in = getClass().getResourceAsStream("/log4j.properties");
        PropertyConfigurator.configure(in);
    }
}
