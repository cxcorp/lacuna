package cx.corp.lacuna.core.macos;

import cx.corp.lacuna.core.PidEnumerator;
import cx.corp.lacuna.core.ProcessEnumerationException;
import cx.corp.lacuna.core.macos.darwinapi.DarwinConstants;
import cx.corp.lacuna.core.macos.darwinapi.Libc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MacPidEnumerator implements PidEnumerator {

    private final Libc libc;

    public MacPidEnumerator(Libc libc) {
        this.libc = Objects.requireNonNull(libc, "libc cannot be null!");
    }

    @Override
    public List<Integer> getPids() throws ProcessEnumerationException {
        int[] pidBuffer = createMaxSizePidBuffer();
        int pidCount = fillBufferWithProcessIds(pidBuffer);
        return Arrays.stream(pidBuffer, 0, pidCount)
            .boxed()
            .collect(Collectors.toList());
    }

    private int[] createMaxSizePidBuffer() {
        return new int[DarwinConstants.PID_MAX];
    }

    private int fillBufferWithProcessIds(int[] pidBuffer) {
        return libc.proc_listallpids(pidBuffer, 4 * pidBuffer.length);
    }
}
