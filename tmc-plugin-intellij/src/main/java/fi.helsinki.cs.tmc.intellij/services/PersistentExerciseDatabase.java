package fi.helsinki.cs.tmc.intellij.services;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;


@State(
        name = "TmcExerciseDatabase",
        storages = { @Storage("TmcExerciseDatabase.xml") }
        )
public class PersistentExerciseDatabase implements
        PersistentStateComponent<PersistentExerciseDatabase> {

    private ExerciseDatabase exerciseDatabase;

    public ExerciseDatabase getExerciseDatabase() {
        return exerciseDatabase;
    }

    public void setExerciseDatabase(ExerciseDatabase exerciseDatabase) {
        this.exerciseDatabase = exerciseDatabase;
    }

    @Nullable
    @Override
    public PersistentExerciseDatabase getState() {
        return this;
    }

    @Override
    public void loadState(PersistentExerciseDatabase persistentExerciseDatabase) {
        XmlSerializerUtil.copyBean(persistentExerciseDatabase, this);
    }

    @Nullable
    public static PersistentExerciseDatabase getInstance() {
        return ServiceManager.getService(PersistentExerciseDatabase.class);
    }

}
