package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;

public class WindowsNativeProcessCollector implements NativeProcessCollector {

    private final ProcessOpener processOpener;
    private final ProcessOwnerGetter ownerGetter;
    private final ProcessDescriptionGetter descriptionGetter;

    public WindowsNativeProcessCollector(ProcessOpener processOpener,
                                         ProcessOwnerGetter ownerGetter,
                                         ProcessDescriptionGetter descriptionGetter) {
        if (processOpener == null || ownerGetter == null || descriptionGetter == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        this.processOpener = processOpener;
        this.ownerGetter = ownerGetter;
        this.descriptionGetter = descriptionGetter;
    }

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
