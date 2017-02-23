package cx.corp.lacuna.ui.presenter;

public interface ProcessListCallbacks {
    /**
     * Triggered when the view has chosen a process from the list.
     */
    void processChosen();

    /**
     * Triggered when the view has requested the process list to be updated.
     */
    void updateRequested();
}
