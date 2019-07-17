package cx.corp.lacuna.core.macos.darwinapi;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Proxy interface to the macOS {@code libc.dylib} library.
 * @see DarwinApiBootstrapper
 * @cx.darwinapiinterface
 */
public interface Libc extends Library {

    int proc_listallpids(int[] buffer, int bufferSize);

    int proc_pidinfo(int pid,
                     int flavor,
                     long arg,
                     ProcBsdShortInfo outStructure,
                     int structSize);

    int proc_name(int pid, byte[] buffer, int bufferSize);

    /**
     * Gets the user information for the specified UID.
     * @param uid user ID of the user to get
     * @return user information of the specified user, or null if uid was not found
     */
    Passwd getpwuid(int uid);


    class ProcBsdShortInfo extends Structure {

        public int pid, ppid, pgid, status;
        public byte[] comm = new byte[16];
        public int flags, uid, gid, ruid, rgid, svuid, svgid, rfu;

        public ProcBsdShortInfo() {}

        public ProcBsdShortInfo(Pointer ptr) {
            super(ptr);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                "pid", "ppid", "pgid", "status", "comm1", "comm2", "flags",
                "uid", "gid", "ruid", "rgid", "svuid", "svgid", "rfu"
            );
        }
    }

    class Passwd extends Structure {
        public String name;
        public String not_supported;
        public int uid, gid;
        public long change;
        public String klass;
        public String gecos;
        public String dir;
        public String shell;
        public long expire;

        public Passwd() {}

        public Passwd(Pointer p) {
            super(p);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                "name", "not_supported", "uid", "gid", "change",
                "klass", "gecos", "dir", "shell", "expire"
            );
        }
    }
}
