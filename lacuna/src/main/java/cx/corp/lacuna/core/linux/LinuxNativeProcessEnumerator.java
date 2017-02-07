package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.domain.NativeProcess;

import java.util.List;
import java.util.stream.Collectors;

public class LinuxNativeProcessEnumerator implements NativeProcessEnumerator {

    private final NativeProcessCollector processCollector;
    private final PidEnumerator pidEnumerator;

    public LinuxNativeProcessEnumerator(PidEnumerator pidEnumerator, NativeProcessCollector processCollector) {
        if (pidEnumerator == null || processCollector == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }

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
