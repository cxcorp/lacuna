package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Optional;

public class WinApiNativeProcessCollector implements NativeProcessCollector {

    private final Advapi32 advapi;
    private final Kernel32 kernel;

    public WinApiNativeProcessCollector(Kernel32 kernel, Advapi32 advapi) {
        if (kernel == null || advapi == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        this.advapi = advapi;
        this.kernel = kernel;
    }

    public NativeProcess collect(int pid) {
        NativeProcess process = new NativeProcessImpl();
        process.setPid(pid);

        int processHandle = openProcessForInformationReading(pid);
        if (processHandle == WinApiConstants.NULLPTR) {
            process.setDescription(NativeProcess.UNKNOWN_DESCRIPTION);
            process.setOwner(NativeProcess.UNKNOWN_OWNER);
            return process;
        }

        process.setDescription(getProcessDescription(processHandle));
        process.setOwner(getProcessOwner(processHandle));
        closeHandleIfNotNull(processHandle);
        return process;
    }

    private int openProcessForInformationReading(int pid) {
        return kernel.openProcess(ProcessAccessFlags.QUERY_INFORMATION, false, pid);
    }

    private String getProcessDescription(int processHandle) {
        return getProcessImageName(processHandle)
            .orElse(NativeProcess.UNKNOWN_DESCRIPTION);
    }

    private Optional<String> getProcessImageName(int processHandle) {
        char[] nameBuf = new char[WinApiConstants.MAX_FILENAME_LENGTH];
        IntByReference bufferSize = new IntByReference(nameBuf.length);

        boolean success =
            kernel.queryFullProcessImageNameW(
                processHandle,
                WinApiConstants.QUERYFULLPROCESSIMAGENAME_PATHFORMAT_WIN32,
                nameBuf,
                bufferSize);

        // bufferSize gets updated with the amount of written characters
        // as a consequence of a successful call
        return success
            ? Optional.of(new String(nameBuf, 0, bufferSize.getValue()))
            : Optional.empty();
    }

    private String getProcessOwner(int processHandle) {
        // Get token for process
        Optional<Integer> token = getProcessToken(processHandle);
        if (!token.isPresent()) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        // First find out how big of a buffer we need...
        Optional<Integer> neededBufferLength = findNeededTokenInformationBufferLength(token.get());
        if (!neededBufferLength.isPresent()) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        // Then get the information with a properly sized buffer...
        Optional<Advapi32.TokenUser> user = getTokenUser(token.get(), neededBufferLength.get());
        if (!user.isPresent()) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        // includes null terminator!
        Optional<Integer> nameBufferLen = findNeededTokenUserNameBufferLength(user.get().user);
        if (!nameBufferLen.isPresent()) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        Optional<String> tokenUserName = lookupTokenUserName(user.get().user, nameBufferLen.get());
        if (!tokenUserName.isPresent()) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        return tokenUserName.get();
    }

    private Optional<Integer> getProcessToken(int processHandle) {
        IntByReference token = new IntByReference(0);
        boolean success =
            advapi.openProcessToken(
                processHandle,
                WinApiConstants.OPENPROCESSTOKEN_TOKEN_QUERY,
                token);
        return success ? Optional.of(token.getValue()) : Optional.empty();
    }

    private Optional<Integer> findNeededTokenInformationBufferLength(int processToken) {
        IntByReference bytesNeeded = new IntByReference(0);
        boolean success =
            advapi.getTokenInformation(
                processToken,
                WinApiConstants.GETTOKENINFORMATION_TOKENUSER,
                null,
                0,
                bytesNeeded);

        return !success && callFailedBecauseBufferWasTooSmall()
            ? Optional.of(bytesNeeded.getValue())
            : Optional.empty();
    }

    private Optional<Advapi32.TokenUser> getTokenUser(int token, int infoBufferLength) {
        Memory memory = new Memory(infoBufferLength);
        IntByReference bufferLen = new IntByReference(infoBufferLength);
        boolean success =
            advapi.getTokenInformation(
                token,
                WinApiConstants.GETTOKENINFORMATION_TOKENUSER,
                memory,
                (int) memory.size(),
                bufferLen);

        if (!success) {
            return Optional.empty();
        }

        try {
            Advapi32.TokenUser user = new Advapi32.TokenUser(memory);
            return Optional.of(user);
        } catch (Error | Exception ex) {
            return Optional.empty();
        }
    }

    private Optional<Integer> findNeededTokenUserNameBufferLength(int userPsid) {
        IntByReference nameLength = new IntByReference(0);
        IntByReference domainLength = new IntByReference(0);
        IntByReference ignored = new IntByReference(0);

        boolean success =
            advapi.lookupAccountSidW(
                WinApiConstants.NULLPTR,
                userPsid,
                null,
                nameLength,
                null,
                domainLength,
                ignored);

        return !success && callFailedBecauseBufferWasTooSmall()
            ? Optional.of(nameLength.getValue())
            : Optional.empty();
    }

    private Optional<String> lookupTokenUserName(int userPsid, int nameBufferLenWithNullTerminator) {
        char[] nameBuffer = new char[nameBufferLenWithNullTerminator];
        IntByReference nameBufNeededLen = new IntByReference(nameBuffer.length);
        char[] domainBuffer = new char[WinApiConstants.MAX_DOMAIN_NAME_LENGTH];
        IntByReference domainBufNeededLen = new IntByReference(domainBuffer.length);
        IntByReference ignored = new IntByReference(0);

        boolean success =
            advapi.lookupAccountSidW(
                WinApiConstants.NULLPTR,
                userPsid,
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

    private boolean callFailedBecauseBufferWasTooSmall() {
        return Native.getLastError() == SystemErrorCode.INSUFFICIENT_BUFFER.getSystemErrorId();
    }

    private void closeHandleIfNotNull(int handle) {
        if (handle != WinApiConstants.NULLPTR) {
            kernel.closeHandle(handle);
        }
    }
}
