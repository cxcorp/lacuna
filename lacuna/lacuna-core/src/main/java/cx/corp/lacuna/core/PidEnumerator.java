package cx.corp.lacuna.core;

import java.util.List;

/**
 * Provides functionality for fetching a list of all of the running processes'
 * identifiers.
 *
 * <p>Users are encouraged to use {@link NativeProcessEnumerator} if
 * process details such as owner name or command line are needed.
 * @see NativeProcessEnumerator
 */
public interface PidEnumerator {
    List<Integer> getPids() throws ProcessEnumerationException;
}
