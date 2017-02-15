package cx.corp.lacuna.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum TypeSize {
    // deal with it ARM, PowerPC, and C standards before C99
    BOOLEAN(1),
    BYTE(1),
    CHAR_UTF8(1),
    CHAR_UTF16LE(2),
    SHORT(2),
    INT(4),
    FLOAT(4),
    LONG(8),
    DOUBLE(8);

    private int byteSize;

    TypeSize(int byteSize) {
        this.byteSize = byteSize;
    }

    public int getSize() {
        return byteSize;
    }
}
