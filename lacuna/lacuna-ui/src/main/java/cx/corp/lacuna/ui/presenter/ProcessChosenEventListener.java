package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.core.domain.NativeProcess;

@FunctionalInterface
public interface ProcessChosenEventListener {
    void processChosen(NativeProcess process);
}
