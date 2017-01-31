package cx.corp.lacuna.core.linux;

import cx.corp.lacuna.core.NativeProcess;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface MemoryProvider {
    InputStream open(NativeProcess process) throws IOException;
}
