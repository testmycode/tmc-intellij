package fi.helsinki.cs.tmc.intellij.services.logging;

/**
 * Historically configured a bundled log4j 1.x. IntelliJ Platform 2024.1+ no longer ships
 * log4j 1.x and provides its own SLF4J binding, so this class is now a no-op kept only to
 * preserve the existing API surface used by {@code StartupEvent.setupLoggers}.
 */
public class PropertySetter {

    public void setLog4jProperties() {
        // Intentionally left blank: SLF4J output is routed to idea.log by the platform binding.
    }
}
