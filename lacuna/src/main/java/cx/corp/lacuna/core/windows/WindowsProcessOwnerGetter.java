package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Optional;

public class WindowsProcessOwnerGetter implements ProcessOwnerGetter {

    private final ProcessTokenOpener tokenOpener;
    private final TokenUserFinder tokenUserFinder;
    private final Advapi32 advapi;
    private final CloseHandle handleCloser;

    public WindowsProcessOwnerGetter(Advapi32 advapi, CloseHandle handleCloser) {
        if (advapi == null || handleCloser == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        this.advapi = advapi;
        this.handleCloser = handleCloser;
        tokenOpener = new ProcessTokenOpener(advapi, handleCloser);
        tokenUserFinder = new TokenUserFinder(advapi);
    }

    @Override
    public Optional<String> get(ProcessHandle processHandle) {
        if (processHandle == null) {
            throw new IllegalArgumentException("processHandle cannot be null!");
        }

        try (ProcessToken token = tokenOpener.openToken(processHandle)) {
            return tokenUserFinder.findTokenUser(token).flatMap(this::getUserName);
        } catch (TokenOpenException ex) {
            // loggerino
            return Optional.empty();
        }
    }

    private Optional<String> getUserName(Advapi32.TokenUser user) {
        // includes null terminator!
        return getUsernameBufferLength(user)
            .flatMap(bufLen -> lookupTokenUserName(user, bufLen));
    }

    private Optional<Integer> getUsernameBufferLength(Advapi32.TokenUser user) {
        IntByReference nameLength = new IntByReference(0);
        IntByReference domainLength = new IntByReference(0);
        IntByReference ignored = new IntByReference(0);

        boolean success =
            advapi.lookupAccountSidW(
                WinApiConstants.NULL,
                user.user,
                null,
                nameLength,
                null,
                domainLength,
                ignored);

        return !success && callFailedBecauseBufferWasTooSmall()
            ? Optional.of(nameLength.getValue())
            : Optional.empty();
    }

    private boolean callFailedBecauseBufferWasTooSmall() {
        return Native.getLastError() == SystemErrorCode.INSUFFICIENT_BUFFER.getSystemErrorId();
    }

    private Optional<String> lookupTokenUserName(Advapi32.TokenUser user, int nameBufferLenWithNullTerminator) {
        char[] nameBuffer = new char[nameBufferLenWithNullTerminator];
        IntByReference nameBufNeededLen = new IntByReference(nameBuffer.length);
        char[] domainBuffer = new char[WinApiConstants.MAX_DOMAIN_NAME_LENGTH];
        IntByReference domainBufNeededLen = new IntByReference(domainBuffer.length);
        IntByReference ignored = new IntByReference(0);

        boolean success =
            advapi.lookupAccountSidW(
                WinApiConstants.NULL,
                user.user,
                nameBuffer,
                nameBufNeededLen,
                domainBuffer,
                domainBufNeededLen,
                ignored);

        int lengthWithoutNullTerminator = nameBufferLenWithNullTerminator - 1;
        return success
            ? Optional.of(new String(nameBuffer, 0, lengthWithoutNullTerminator))
            : Optional.empty();
    }
}
