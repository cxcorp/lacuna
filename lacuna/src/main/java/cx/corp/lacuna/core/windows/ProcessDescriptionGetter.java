package cx.corp.lacuna.core.windows;

import java.util.Optional;

/**
 * Provides functionality for getting the description of a native process.
 * @see ProcessOpener
 * @see ProcessOwnerGetter
 */
@FunctionalInterface
public interface ProcessDescriptionGetter {
    /**
     * Gets the description of the specified process, or {@link Optional#empty()} if getting fails.
     * @param processHandle Handle to the process.
     * @return The description of the process, or {@link Optional#empty()} if getting fails.
     */
    Optional<String> get(ProcessHandle processHandle);
}
