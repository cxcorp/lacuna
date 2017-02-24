package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;

import java.util.Objects;

public class WindowsNativeProcessCollector implements NativeProcessCollector {

    private final ProcessOpener processOpener;
    private final ProcessOwnerGetter ownerGetter;
    private final ProcessDescriptionGetter descriptionGetter;

    /**
     * Constructs a new {@code WindowsNativeProcessCollector} with the specified
     * process opener, process owner getter, and process description getter.
     * @param processOpener the process handle opener.
     * @param ownerGetter the process owner getter.
     * @param descriptionGetter the process description getter.
     */
    public WindowsNativeProcessCollector(ProcessOpener processOpener,
                                         ProcessOwnerGetter ownerGetter,
                                         ProcessDescriptionGetter descriptionGetter) {
        Objects.requireNonNull(processOpener, "processOpener cannot be null!");
        Objects.requireNonNull(ownerGetter, "ownerGetter cannot be null!");
        Objects.requireNonNull(descriptionGetter, "descriptionGetter cannot be null!");
        this.processOpener = processOpener;
        this.ownerGetter = ownerGetter;
        this.descriptionGetter = descriptionGetter;
    }

    @Override
    public NativeProcess collect(int pid) {
        NativeProcess process = new NativeProcessImpl(
            pid,
            NativeProcess.UNKNOWN_DESCRIPTION,
            NativeProcess.UNKNOWN_OWNER);

        try (ProcessHandle handle = processOpener.open(pid, ProcessAccessFlags.QUERY_INFORMATION)) {
            String description = descriptionGetter.get(handle).orElse(NativeProcess.UNKNOWN_DESCRIPTION);
            String owner = ownerGetter.get(handle).orElse(NativeProcess.UNKNOWN_OWNER);
            process.setDescription(description);
            process.setOwner(owner);
        } catch (ProcessOpenException ex) {
            // TODO: log
        }

        return process;
    }
}
