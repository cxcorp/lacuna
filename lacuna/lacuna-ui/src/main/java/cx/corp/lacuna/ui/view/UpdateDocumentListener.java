package cx.corp.lacuna.ui.view;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.util.function.Consumer;

/**
 * Provides one-way binding for a document.
 */
class UpdateDocumentListener implements DocumentListener {

    private final JTextComponent component;
    private final Consumer<String> callback;

    private UpdateDocumentListener(JTextComponent component, Consumer<String> callback) {
        if (callback == null || component == null) {
            throw new IllegalArgumentException("Arguments cannot be null1");
        }
        this.component = component;
        this.callback = callback;
    }

    public static void addTo(JTextComponent component, Consumer<String> callback) {
        DocumentListener listener = new UpdateDocumentListener(component, callback);
        component.getDocument().addDocumentListener(listener);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        callback.accept(component.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        callback.accept(component.getText());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        callback.accept(component.getText());
    }
}
