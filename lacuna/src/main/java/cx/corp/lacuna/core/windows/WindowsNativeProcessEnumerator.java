package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.PidEnumerator;

import java.util.List;
import java.util.stream.Collectors;

public class WindowsNativeProcessEnumerator implements NativeProcessEnumerator {

    private final NativeProcessCollector collector;
    private final PidEnumerator pidEnumerator;

    public WindowsNativeProcessEnumerator(PidEnumerator pidEnumerator, NativeProcessCollector collector) {
        if (pidEnumerator == null || collector == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }

        this.pidEnumerator = pidEnumerator;
        this.collector = collector;
    }

    @Override
    public List<NativeProcess> getProcesses() {
        return pidEnumerator
            .getPids()
            .mapToObj(collector::collect)
            .collect(Collectors.toList());
    }
}
