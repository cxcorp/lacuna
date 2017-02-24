package cx.corp.lacuna.ui.view;

/**
 * Represents a view in the MVP pattern.
 * @param <TCallbacks> the type of the callbacks used by the view.
 */
public interface View<TCallbacks> {
    /**
     * Attaches the provided callbacks to this view.
     * @param callbacks the callbacks.
     */
    void attach(TCallbacks callbacks);
}
