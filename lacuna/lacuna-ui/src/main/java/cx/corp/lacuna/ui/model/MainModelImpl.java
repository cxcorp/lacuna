package cx.corp.lacuna.ui.model;

import cx.corp.lacuna.core.domain.NativeProcess;

public class MainModelImpl implements MainModel {

    private NativeProcess activeProcess = null;

    @Override
    public void setActiveProcess(NativeProcess pid) {
        activeProcess = pid;
    }

    @Override
    public NativeProcess getActiveProcess() {
        return activeProcess;
    }
}
