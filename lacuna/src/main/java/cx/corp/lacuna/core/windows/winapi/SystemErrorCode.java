package cx.corp.lacuna.core.windows.winapi;

import java.util.HashMap;
import java.util.Map;

public enum SystemErrorCode {

    // https://msdn.microsoft.com/en-us/library/windows/desktop/ms681381(v=vs.85).aspx

    INSUFFICIENT_BUFFER(122, "The data area passed to a system call is too small"),
    PARTIAL_COPY(299, "Only part of a ReadProcessMemory or WriteProcessMemory request was completed"),
    NO_ACCESS(998, "Invalid access to memory location"),
    UNMAPPED(-1, "This system error message has not been mapped");

    private static final Map<Integer, SystemErrorCode> ID_TO_ERRORCODE;
    static {
        ID_TO_ERRORCODE = new HashMap<>();
        for (SystemErrorCode error : values()) {
            ID_TO_ERRORCODE.put(error.id, error);
        }
    }

    private final int id;
    private final String error;

    SystemErrorCode(int id, String error) {
        this.id = id;
        this.error = error;
    }

    public static SystemErrorCode fromId(int id) {
        SystemErrorCode errorCode = ID_TO_ERRORCODE.get(id);
        return errorCode == null ? UNMAPPED : errorCode;
    }

    public int getId() {
        return id;
    }

    public String getError() {
        return error;
    }
}
