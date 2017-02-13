package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

public interface RawMemoryWriter {
    void write(NativeProcess process, int offset, byte[] buffer) throws MemoryAccessException;
}
