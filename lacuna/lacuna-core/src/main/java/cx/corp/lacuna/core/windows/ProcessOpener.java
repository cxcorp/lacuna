package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.ProcessOpenException;

/**
 * Provides functionality for opening a handle to a native process.
 * @see ProcessDescriptionGetter
 * @see ProcessOwnerGetter
 * @see ProcessHandle
 */
@FunctionalInterface
public interface ProcessOpener {
    /**
     * Opens a handle to the native process.
     * @param pid The process identifier of the native process.
     * @param processAccessFlags The {@link cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags}
     *                           used to specify requested access rights.
     * @return A handle to the native process.
     * @throws ProcessOpenException if opening a handle failed, for example due to
     *                              insufficient privileges.
     */
    ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException;
}
