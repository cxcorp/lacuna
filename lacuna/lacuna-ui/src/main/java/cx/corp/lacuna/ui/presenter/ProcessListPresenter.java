package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.ui.model.ProcessListModel;
import cx.corp.lacuna.ui.view.ProcessListView;

import java.util.ArrayList;
import java.util.List;

public class ProcessListPresenter implements Presenter, ProcessListCallbacks {

    private List<ProcessChosenEventListener> processChosenCallbacks;
    private final ProcessListView view;
    private final ProcessListModel model;

    public ProcessListPresenter(ProcessListView view, ProcessListModel model) {
        if (view == null || model == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        this.view = view;
        this.model = model;
        this.processChosenCallbacks = new ArrayList<>();
    }

    @Override
    public void processChosen() {
        view.getChosenProcessId()
            .ifPresent(pid -> processChosenCallbacks.forEach(c -> c.processChosen(pid)));
    }

    @Override
    public void updateRequested() {
        view.setProcessList(model.getProcesses());
    }

    @Override
    public void initialize() {
        view.attach(this);
    }

    public void addProcessChosenListener(ProcessChosenEventListener callback) {
        processChosenCallbacks.add(callback);
    }
}
