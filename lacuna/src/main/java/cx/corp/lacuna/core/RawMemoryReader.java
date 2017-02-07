package cx.corp.lacuna.core;

import cx.corp.lacuna.core.domain.NativeProcess;

import java.nio.ByteBuffer;

public interface RawMemoryReader {
    ByteBuffer read(NativeProcess process, int offset, int bytesToRead);
}
