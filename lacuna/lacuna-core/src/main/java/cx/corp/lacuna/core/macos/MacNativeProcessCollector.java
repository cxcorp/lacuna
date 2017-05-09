package cx.corp.lacuna.core.macos;

import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.macos.darwinapi.DarwinApiBootstrapper;
import cx.corp.lacuna.core.macos.darwinapi.DarwinConstants;
import cx.corp.lacuna.core.macos.darwinapi.Libc;

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
        return passwd == null ? Optional.empty() : Optional.of(passwd.name);
    }

    private Optional<String> getDescription(int pid) {
        byte[] buffer = new byte[1024];
        int bytesWritten = libc.proc_name(pid, buffer, buffer.length);

        if (bytesWritten > 0) {
            // no clue if it's utf-8
            return Optional.of(new String(buffer, StandardCharsets.UTF_8));
        }

        // no privileges for proc_name, try the comm description instead
        Libc.ProcBsdShortInfo info = getProcessInfo(pid);
        if (info == null) {
            return Optional.empty();
        }

        return Optional.of(new String(info.comm, StandardCharsets.UTF_8));
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
}
