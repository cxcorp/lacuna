package cx.corp.lacuna.core.macos.darwinapi;

import com.sun.jna.Library;

public interface Libc extends Library {
    int proc_listallpids(int[] buffer, int bufferSize);
}
