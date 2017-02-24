package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * <p>This implementation first fetches all processes with a
 * {@link PidEnumerator}, then collects the processes' details with a
 * {@link NativeProcessCollector}.
 */
public class NativeProcessEnumeratorImpl implements NativeProcessEnumerator {
    private final NativeProcessCollector processCollector;
    private final PidEnumerator pidEnumerator;

    /**
     * Constructs a new {@code NativeProcessEnumeratorImpl} with the specified
     * PID enumerator and process detail collector.
     * @param pidEnumerator the PID enumerator.
     * @param processCollector the process detail collector.
     * @throws NullPointerException if {@code pidEnumerator} or
     *                              {@code processCollector} is null.
     */
    public NativeProcessEnumeratorImpl(PidEnumerator pidEnumerator,
                                       NativeProcessCollector processCollector) {
        Objects.requireNonNull(pidEnumerator, "pidEnumerator cannot be null!");
        Objects.requireNonNull(processCollector, "processCollector cannot be null!");
        this.pidEnumerator = pidEnumerator;
        this.processCollector = processCollector;
    }

    @Override
    public List<NativeProcess> getProcesses() {
        return pidEnumerator
            .getPids()
            .stream()
            .map(processCollector::collect)
            .collect(Collectors.toList());
    }
}
