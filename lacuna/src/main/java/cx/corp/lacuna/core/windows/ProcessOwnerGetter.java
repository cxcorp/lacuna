package cx.corp.lacuna.core.windows;

import java.util.Optional;

/**
 * Provides functionality for getting the owner of a native process.
 * @see ProcessOpener
 * @see ProcessDescriptionGetter
 */
@FunctionalInterface
public interface ProcessOwnerGetter {
    /**
     * Gets the owner of the specified process, or {@link Optional#empty()} if getting fails.
     * @param processHandle Handle to the process.
     * @return The owner of the specified process, or {@link Optional#empty()} if getting fails.
     */
    Optional<String> get(ProcessHandle processHandle);
}
