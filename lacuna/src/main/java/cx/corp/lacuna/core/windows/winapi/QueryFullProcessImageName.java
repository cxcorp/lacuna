package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface QueryFullProcessImageName extends StdCallLibrary {
    boolean queryFullProcessImageNameW(int hProcess,
                                       int dwFlags,
                                       char[] lpExeName,
                                       IntByReference lpdwSize);
}
