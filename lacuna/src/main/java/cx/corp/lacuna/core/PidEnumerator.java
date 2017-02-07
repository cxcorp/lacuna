package cx.corp.lacuna.core;

import java.util.List;

@FunctionalInterface
public interface PidEnumerator {
    List<Integer> getPids() throws ProcessEnumerationException;
}
