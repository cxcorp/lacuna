package cx.corp.lacuna.ui.model;

public class MainModelImpl implements MainModel {

    private int activeProcess = -1;

    @Override
    public void setActiveProcess(int pid) {
        activeProcess = pid;
    }

    @Override
    public int getActiveProcess() {
        return activeProcess;
    }
}
