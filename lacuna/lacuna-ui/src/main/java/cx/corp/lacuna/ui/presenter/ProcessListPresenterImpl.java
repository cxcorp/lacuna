package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.ui.model.ProcessListModel;
import cx.corp.lacuna.ui.view.ProcessListView;

public class ProcessListPresenterImpl implements ProcessListPresenter, ProcessListCallbacks {

    private final ProcessListView view;
    private final ProcessListModel model;

    public ProcessListPresenterImpl(ProcessListView view, ProcessListModel model) {
        if (view == null || model == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        this.view = view;
        this.model = model;
    }

    @Override
    public void processChosen() {

    }

    @Override
    public void updateRequested() {
        view.setProcessList(model.getProcesses());
    }
}
