package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.win32.StdCallLibrary;

public interface GetProcessId extends StdCallLibrary {
    int getProcessId(int handle);
}
