package cx.corp.lacuna.core.windows;

import java.util.List;

@FunctionalInterface
public interface PidEnumerator {
    List<Integer> getPids();
}
