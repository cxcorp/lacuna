package cx.corp.lacuna.core;

import java.util.List;

public interface NativeProcessEnumerator {
    /** Retrieves a list of all processes.
     *
     * @return A list of all processes.
     */
    List<NativeProcess> getProcesses();
}
