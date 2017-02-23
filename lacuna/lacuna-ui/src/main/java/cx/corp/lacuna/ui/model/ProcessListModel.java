package cx.corp.lacuna.ui.model;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.List;

public class ProcessListModel {

    private final SettingsModel settings;

    public ProcessListModel(SettingsModel settings) {
        this.settings = settings;
    }

    public List<NativeProcess> getProcesses() {
        return settings.getBootstrap().getNativeProcessEnumerator().getProcesses();
    }
}
