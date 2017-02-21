package cx.corp.lacuna.ui.model;

import cx.corp.lacuna.core.domain.NativeProcess;

public interface MainModel {
    void setActiveProcess(NativeProcess pid);
    NativeProcess getActiveProcess();
}
