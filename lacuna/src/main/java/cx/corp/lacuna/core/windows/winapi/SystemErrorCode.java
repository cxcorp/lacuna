package cx.corp.lacuna.core.windows.winapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines standard WinAPI system errors with their descriptions.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681381(v=vs.85).aspx">System Error Codes (Windows)</a>
 */
public enum SystemErrorCode {

    /**
     * 122 - The data area passed to a system call is too small.
     */
    INSUFFICIENT_BUFFER(122, "The data area passed to a system call is too small"),

    /**
     * 299 - Only part of a ReadProcessMemory or WriteProcessMemory request was completed.
     */
    PARTIAL_COPY(299, "Only part of a ReadProcessMemory or WriteProcessMemory request was completed"),

    /**
     * 998 - Invalid access to memory location.
     */
    NO_ACCESS(998, "Invalid access to memory location");

    private static final Map<Integer, SystemErrorCode> ID_TO_ERRORCODE;

    static {
        ID_TO_ERRORCODE = new HashMap<>();
        for (SystemErrorCode error : values()) {
            ID_TO_ERRORCODE.put(error.systemErrorId, error);
        }
    }

    private final int systemErrorId;
    private final String description;

    SystemErrorCode(int id, String error) {
        this.systemErrorId = id;
        this.description = error;
    }

    /**
     * Returns a {@link SystemErrorCode} enum constant with the matching
     * system error code constant, or {@code null} if none found.
     *
     * @param id A WinAPI system description code constant.
     * @return A matching {@link SystemErrorCode} enum constant or {@code null} if none found.
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms681381(v=vs.85).aspx">System Error Codes (Windows)</a>
     */
    public static SystemErrorCode fromId(int id) {
        return ID_TO_ERRORCODE.get(id);
    }

    /**
     * Gets the WinAPI system description identifier of the enum constant.
     *
     * @return The WinAPI system description identifier.
     */
    public int getSystemErrorId() {
        return systemErrorId;
    }

    /**
     * Gets the description of the system error.
     *
     * @return The description of the system error.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return systemErrorId + ": " + description;
    }
}
