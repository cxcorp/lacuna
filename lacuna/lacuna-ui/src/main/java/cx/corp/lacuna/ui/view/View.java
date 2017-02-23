package cx.corp.lacuna.ui.view;

public interface View<TCallbacks> {
    void attach(TCallbacks callbacks);
}
