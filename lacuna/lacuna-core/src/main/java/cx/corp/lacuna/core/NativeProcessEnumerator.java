package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.List;

/**
 * Provides functionality for fetching a list of all running processes on the
 * host computer.
 *
 * <p>Users are encouraged to use {@link PidEnumerator} if process details like
 * owner name or command line are not needed.
 *
 * @see PidEnumerator
 */
public interface NativeProcessEnumerator {
    /**
     * Retrieves a list of all running processes.
     *
     * @return A list of all running processes.
     */
    List<NativeProcess> getProcesses() throws ProcessEnumerationException;
}
