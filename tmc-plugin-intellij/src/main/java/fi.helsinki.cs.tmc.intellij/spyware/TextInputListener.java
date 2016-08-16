package fi.helsinki.cs.tmc.intellij.spyware;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import fi.helsinki.cs.tmc.core.communication.serialization.JsonMaker;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.spyware.LoggableEvent;
import fi.helsinki.cs.tmc.intellij.services.CourseAndExerciseManager;
import fi.helsinki.cs.tmc.intellij.services.ObjectFinder;
import fi.helsinki.cs.tmc.intellij.services.PathResolver;
import name.fraser.neil.plaintext.DiffMatchPatch;

import java.util.List;


public class TextInputListener implements DocumentListener {

    private DiffMatchPatch diff = new DiffMatchPatch();
    private String previous;
    private String modified;

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {
        previous = documentEvent.getDocument().getText();
    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {
        modified = documentEvent.getDocument().getText();
        if (isThisCorrectProject()) {
            if (makeSureChangeIsNotJustWhitespace(documentEvent)) {
                createPatches(PathResolver.
                        getExercise(ObjectFinder.findCurrentProject().getBasePath()), documentEvent);
            }
        }
    }

    private boolean isThisCorrectProject() {
        return CourseAndExerciseManager.isCourseInDatabase(PathResolver
                .getCourseName(ObjectFinder.findCurrentProject().getBasePath()));
    }

    private boolean makeSureChangeIsNotJustWhitespace(DocumentEvent documentEvent) {
        return !documentEvent.
                getNewFragment().toString().trim().isEmpty() || !documentEvent.
                getOldFragment().toString().trim().isEmpty();
    }

    private void createPatches(Exercise exercise, DocumentEvent documentEvent) {
        List<DiffMatchPatch.Patch> patches;
        patches = diff.patch_make(previous, modified);

        if (isRemoveEvent(documentEvent)) {
            addEventToManager(exercise, "text_remove", generatePatchDescription(documentEvent, patches));
        } else if (isPasteEvent(documentEvent)) {
            addEventToManager(exercise, "text_paste", generatePatchDescription(documentEvent, patches));
        } else {
            addEventToManager(exercise, "text_insert", generatePatchDescription(documentEvent, patches));
        }
    }

    private boolean isPasteEvent(DocumentEvent documentEvent) {
        return (documentEvent.getNewLength() > 2);
    }

    private String generatePatchDescription(DocumentEvent documentEvent,
                                            List<DiffMatchPatch.Patch> patches) {
        String source = documentEvent.getSource().toString();
        source = source.substring(20, source.length() - 1);
        return JsonMaker.create()
                .add("file", source)
                .add("patches", diff.patch_toText(patches))
                .toString();
    }

    private boolean isRemoveEvent(DocumentEvent documentEvent) {
        return (documentEvent.getOldLength() > 0 && documentEvent.getNewLength() == 0);
    }

    private void addEventToManager(Exercise exercise, String eventType, String text) {
        LoggableEvent event = new LoggableEvent(exercise, eventType, text.getBytes());
        System.out.println(event);
        SpywareEventManager.add(event);
    }

}
