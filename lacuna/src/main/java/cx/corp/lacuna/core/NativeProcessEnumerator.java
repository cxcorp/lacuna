package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.List;

public interface NativeProcessEnumerator {
    /**
     * Retrieves a list of all processes.
     *
     * @return A list of all processes.
     */
    List<NativeProcess> getProcesses();
}
