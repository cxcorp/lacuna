package cx.corp.lacuna.core.windows;

import java.util.stream.IntStream;

@FunctionalInterface
public interface PidEnumerator {
    IntStream getPids();
}
