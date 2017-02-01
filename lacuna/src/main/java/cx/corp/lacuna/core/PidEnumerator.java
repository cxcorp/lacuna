package cx.corp.lacuna.core;

import java.util.stream.IntStream;

@FunctionalInterface
public interface PidEnumerator {
    IntStream getPids();
}
