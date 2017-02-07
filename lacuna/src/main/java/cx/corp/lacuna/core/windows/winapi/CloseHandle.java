package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.win32.StdCallLibrary;

public interface CloseHandle extends StdCallLibrary {
    boolean closeHandle(int handle);
}
