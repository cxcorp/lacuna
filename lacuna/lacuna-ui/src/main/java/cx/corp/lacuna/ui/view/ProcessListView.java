package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.Collection;

public interface ProcessListView {
    void setProcessList(Collection<NativeProcess> processes);
    int getChosenProcessId();
}
