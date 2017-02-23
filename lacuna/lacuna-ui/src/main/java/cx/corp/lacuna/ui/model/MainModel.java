package cx.corp.lacuna.ui.model;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.Observable;

public class MainModel extends Observable {

    private NativeProcess activeProcess;

    public void setActiveProcess(NativeProcess pid) {
        activeProcess = pid;
        setChanged();
        notifyObservers();
    }

    public NativeProcess getActiveProcess() {
        return activeProcess;
    }
}
