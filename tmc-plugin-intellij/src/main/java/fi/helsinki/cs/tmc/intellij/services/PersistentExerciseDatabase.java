package fi.helsinki.cs.tmc.intellij.services;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Uses the IDE to save settings on disk
 * Defined in plugin.xml on line in extensions group
 * <applicationService serviceImplementation
 *   ="fi.helsinki.cs.tmc.intellij.services.PersistentExerciseDatabase"/>
 */
@State(
        name = "TmcExerciseDatabase",
        storages = { @Storage("TmcExerciseDatabase.xml") }
        )
public class PersistentExerciseDatabase implements
        PersistentStateComponent<PersistentExerciseDatabase> {

    private static final Logger logger = LoggerFactory.getLogger(PersistentExerciseDatabase.class);

    private ExerciseDatabase exerciseDatabase;

    public ExerciseDatabase getExerciseDatabase() {
        logger.info("Getting ExerciseDatabase. @PersistentExerciseDatabase.");
        return exerciseDatabase;
    }

    public void setExerciseDatabase(ExerciseDatabase exerciseDatabase) {
        logger.info("Setting ExerciseDatabase. @PersistentExerciseDatabase.");
        this.exerciseDatabase = exerciseDatabase;
    }

    @Nullable
    @Override
    public PersistentExerciseDatabase getState() {
        logger.info("Processing getState. @PersistentExerciseDatabase.");
        return this;
    }

    @Override
    public void loadState(PersistentExerciseDatabase persistentExerciseDatabase) {
        logger.info("Processing loadState. @PersistentExerciseDatabase.");
        XmlSerializerUtil.copyBean(persistentExerciseDatabase, this);
    }

    @Nullable
    public static PersistentExerciseDatabase getInstance() {
        logger.info("Processing getInstance. @PersistentExerciseDatabase.");
        return ServiceManager.getService(PersistentExerciseDatabase.class);
    }

}
