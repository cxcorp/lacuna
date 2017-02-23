package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.model.MainModel;
import cx.corp.lacuna.ui.view.MainView;

public class MainPresenter implements Presenter, MainCallbacks {

    private final MainView view;
    private final MainModel model;

    public MainPresenter(MainView view, MainModel model) {
        if (view == null || model == null) {
            throw new IllegalArgumentException();
        }
        this.view = view;
        this.model = model;
    }

    @Override
    public void initialize() {
        view.attach(this);
    }

    @Override
    public void newActiveProcessSelected() {
        NativeProcess newProc = view.getActiveProcess();
        model.setActiveProcess(newProc);
    }
}
