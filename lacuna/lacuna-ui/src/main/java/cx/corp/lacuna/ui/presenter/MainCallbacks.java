package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.core.domain.NativeProcess;

public interface MainCallbacks {
    void newActiveProcessSelected(NativeProcess newProcess);
}
