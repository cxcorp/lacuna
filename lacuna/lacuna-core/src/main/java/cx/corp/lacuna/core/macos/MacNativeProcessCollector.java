package cx.corp.lacuna.core.macos;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.macos.darwinapi.DarwinApiBootstrapper;
import cx.corp.lacuna.core.macos.darwinapi.DarwinConstants;
import cx.corp.lacuna.core.macos.darwinapi.Libc;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class MacNativeProcessCollector implements NativeProcessCollector {

    // test pls ignore
    public static void main(String... args) {
        Libc libc = new DarwinApiBootstrapper().getLibc();
        MacPidEnumerator enumerator = new MacPidEnumerator(libc);
        MacNativeProcessCollector collector = new MacNativeProcessCollector(libc);
        for (int pid : enumerator.getPids()) {
            System.out.println(collector.collect(pid));
        }
    }

    private final Libc libc;

    public MacNativeProcessCollector(Libc libc) {
        this.libc = Objects.requireNonNull(libc);
    }

    @Override
    public NativeProcess collect(int pid) {
        NativeProcess proc = new NativeProcessImpl();
        proc.setPid(pid);
        proc.setOwner(getOwner(pid).orElse(NativeProcess.UNKNOWN_OWNER));
        proc.setDescription(getDescription(pid).orElse(NativeProcess.UNKNOWN_DESCRIPTION));
        return proc;
    }

    private Optional<String> getOwner(int pid) {
        Libc.ProcBsdShortInfo info = getProcessInfo(pid);
        if (info == null) {
            return Optional.empty();
        }

        Libc.Passwd passwd = libc.getpwuid(info.uid);
        return passwd == null || isNullOrEmptyOrZeroLen(passwd.name)
            ? Optional.empty()
            : Optional.of(passwd.name);
    }

    private Optional<String> getDescription(int pid) {
        byte[] buffer = new byte[1024];
        int bytesWritten = libc.proc_name(pid, buffer, buffer.length);

        if (bytesWritten > 0) {
            // no clue if it's utf-8
            String name = new String(buffer, 0, bytesWritten, StandardCharsets.UTF_8);
            return Optional.of(name);
        }

        // bytesWritten <= 0; no privileges for proc_name,
        // try the comm description instead
        Libc.ProcBsdShortInfo info = getProcessInfo(pid);
        if (info == null) {
            return Optional.empty();
        }

        // comm is max 16 bytes long but we don't know the string's
        // length, find the length
        int firstNull = ArrayUtils.indexOf(info.comm, (byte)'\0');
        if (firstNull <= 0) {
            // comm was empty as well :(
            return Optional.empty();
        }
        String comm = new String(info.comm, 0, firstNull, StandardCharsets.UTF_8);
        return Optional.of(comm);
    }

    private Libc.ProcBsdShortInfo getProcessInfo(int pid) {
        Libc.ProcBsdShortInfo info = new Libc.ProcBsdShortInfo();
        int bytesWritten = libc.proc_pidinfo(
            pid,
            DarwinConstants.PROC_PIDT_SHORTBSDINFO,
            0,
            info,
            info.size()
        );

        return bytesWritten < info.size() ? null : info;
    }

    private static boolean isNullOrEmptyOrZeroLen(String str) {
        return str == null || str.length() == 0 || str.charAt(0) == '\0';
    }
}
