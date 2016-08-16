package fi.helsinki.cs.tmc.intellij.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ActivateSpywareAction implements TypedActionHandler {

    private TypedActionHandler handler;
    private List<Document> listenedDocuments;

    public ActivateSpywareAction(TypedActionHandler originalHandler) {
        handler = originalHandler;
        listenedDocuments = new ArrayList<>();
    }

    @Override
    public void execute(@NotNull final Editor editor, char character,
                        @NotNull DataContext dataContext) {

        if (listenedDocuments.contains(editor.getDocument())) {
            handler.execute(editor, character, dataContext);
            return;
        }

        DocumentListener docListener = new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent documentEvent) {
                if (documentEvent.getNewLength() > 1) {
                    System.out.println("Added : " + documentEvent.getNewFragment());
                    //         System.out.println("Document before edit: \n" + documentEvent.getDocument().getText() + "\nDocument end");
                }
            }

            @Override
            public void documentChanged(DocumentEvent documentEvent) {
                if (documentEvent.getOldLength() > 1) {
                    System.out.println("Removed : " + documentEvent.getOldFragment());
                    System.out.println();
                }
            }
        };
        editor.getCaretModel().getAllCarets().get(0);
        EditorActionManager.getInstance().getTypedAction();

        EditorMouseListener listener = getEditorMouseListener();
        editor.addEditorMouseListener(listener);
        editor.getDocument().addDocumentListener(docListener);
        listenedDocuments.add(editor.getDocument());

        handler.execute(editor, character, dataContext);
    }

    private EditorMouseListener getEditorMouseListener() {
        return new EditorMouseListener() {
            @Override
            public void mousePressed(EditorMouseEvent editorMouseEvent) {

            }

            @Override
            public void mouseClicked(EditorMouseEvent editorMouseEvent) {
            }

            @Override
            public void mouseReleased(EditorMouseEvent editorMouseEvent) {
                System.out.println(editorMouseEvent.getEditor()
                        .getSelectionModel().getSelectedText());

            }

            @Override
            public void mouseEntered(EditorMouseEvent editorMouseEvent) {

            }

            @Override
            public void mouseExited(EditorMouseEvent editorMouseEvent) {

            }
        };
    }
}
