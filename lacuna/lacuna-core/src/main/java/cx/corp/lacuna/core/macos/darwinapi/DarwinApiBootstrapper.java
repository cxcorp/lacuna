package cx.corp.lacuna.core.macos.darwinapi;

import com.sun.jna.Native;

public class DarwinApiBootstrapper {

    public Libc getLibc() {
        return Native.loadLibrary("c", Libc.class);
    }
}
