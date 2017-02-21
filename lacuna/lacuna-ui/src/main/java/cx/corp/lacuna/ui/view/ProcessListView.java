package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.presenter.ProcessListCallbacks;

import java.util.List;
import java.util.Optional;

public interface ProcessListView extends View<ProcessListCallbacks> {
    void setProcessList(List<NativeProcess> processes);
    Optional<NativeProcess> getChosenProcess();
}
