package cx.corp.lacuna.core.windows;

import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.windows.winapi.QueryFullProcessImageName;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Objects;
import java.util.Optional;

/**
 * {@inheritDoc}
 * @cx.useswinapi
 */
public class WindowsProcessDescriptionGetter implements ProcessDescriptionGetter {

    private final QueryFullProcessImageName winapi;

    /**
     * Constructs a new {@code WindowsProcessDescriptionGetter} with the
     * specified {@code QueryFullProcessImageName} Windows API proxy.
     * @param winapi the Windows API {@code QueryFullProcessImageName} proxy.
     * @cx.useswinapi
     */
    public WindowsProcessDescriptionGetter(QueryFullProcessImageName winapi) {
        Objects.requireNonNull(winapi, "winapi cannot be null!");
        this.winapi = winapi;
    }

    @Override
    public Optional<String> get(ProcessHandle processHandle) {
        Objects.requireNonNull(processHandle, "processHandle cannot be null!");
        return getProcessImageName(processHandle.getNativeHandle());
    }

    private Optional<String> getProcessImageName(int processHandle) {
        char[] nameBuf = new char[WinApiConstants.MAX_FILENAME_LENGTH];
        IntByReference bufferSize = new IntByReference(nameBuf.length);

        boolean success =
            winapi.queryFullProcessImageNameW(
                processHandle,
                WinApiConstants.QUERYFULLPROCESSIMAGENAME_PATHFORMAT_WIN32,
                nameBuf,
                bufferSize);

        // bufferSize gets updated with the amount of written characters
        // as a consequence of a successful call
        return success
            ? Optional.of(new String(nameBuf, 0, bufferSize.getValue()))
            : Optional.empty();
    }
}
