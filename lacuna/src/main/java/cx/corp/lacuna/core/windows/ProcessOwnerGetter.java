package cx.corp.lacuna.core.windows;

import java.util.Optional;

@FunctionalInterface
public interface ProcessOwnerGetter {
    Optional<String> get(ProcessHandle processHandle);
}
