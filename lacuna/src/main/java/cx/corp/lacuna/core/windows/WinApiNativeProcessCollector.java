package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.ProcessAccessFlags;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

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
        NativeProcess process = new NativeProcess();
        process.setPid(pid);

        int processHandle = openProcessForInformationReading(pid);
        if (processHandle == WinApiConstants.NULLPTR) {
            process.setDescription(NativeProcess.UNKNOWN_DESCRIPTION);
            process.setOwner(NativeProcess.UNKNOWN_OWNER);
            return process;
        }

        process.setDescription(queryProcessName(processHandle));
        process.setOwner(getProcessOwner(processHandle));
        closeHandleIfNotNull(processHandle);
        return process;
    }

    private int openProcessForInformationReading(int pid) {
        return kernel.openProcess(ProcessAccessFlags.QUERY_INFORMATION, false, pid);
    }

    private String queryProcessName(int processHandle) {
        char[] nameBuf = new char[WinApiConstants.MAX_FILENAME_LENGTH];
        IntByReference bufferSize = new IntByReference(nameBuf.length);

        boolean success =
                kernel.queryFullProcessImageNameW(
                        processHandle,
                        WinApiConstants.QUERYFULLPROCESSIMAGENAME_PATHFORMAT_WIN32,
                        nameBuf,
                        bufferSize);

        if (!success) {
            return NativeProcess.UNKNOWN_DESCRIPTION;
        }

        // bufferSize gets updated with the amount of written characters
        // as a consequence of a successful call
        return new String(nameBuf, 0, bufferSize.getValue());
    }

    private String getProcessOwner(int processHandle) {
        // Get token for process
        IntByReference token = new IntByReference(0);
        boolean success =
            advapi.openProcessToken(
                processHandle,
                WinApiConstants.OPENPROCESSTOKEN_TOKEN_QUERY,
                token);
        if (!success) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        // First find out how big of a buffer we need...
        IntByReference bytesNeeded = new IntByReference(0);
        success =
            advapi.getTokenInformation(
                token.getValue(),
                WinApiConstants.GETTOKENINFORMATION_TOKENUSER,
                null,
                0,
                bytesNeeded);
        if (!success && Native.getLastError() != SystemErrorCode.INSUFFICIENT_BUFFER.getSystemErrorId()) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        // Then get the information with a properly sized buffer...
        Memory memory = new Memory(bytesNeeded.getValue());
        Pointer tokenUserPointer = memory.share(0);
        success =
            advapi.getTokenInformation(
                token.getValue(),
                WinApiConstants.GETTOKENINFORMATION_TOKENUSER,
                tokenUserPointer,
                (int) memory.size(),
                bytesNeeded);

        if (!success) {
            return NativeProcess.UNKNOWN_DESCRIPTION;
        }

        Advapi32.TokenUser user = new Advapi32.TokenUser(tokenUserPointer);
        // Look up name of Owner
        char[] nameBuffer = new char[WinApiConstants.MAX_USERNAME_LENGTH];
        IntByReference nameBufNeededLen = new IntByReference(nameBuffer.length);
        char[] domainBuffer = new char[WinApiConstants.MAX_DOMAIN_NAME_LENGTH];
        IntByReference domainBufNeededLen = new IntByReference(domainBuffer.length);

        IntByReference ignored = new IntByReference(0);

        success =
            advapi.lookupAccountSidW(
                WinApiConstants.NULLPTR,
                user.user,
                nameBuffer,
                nameBufNeededLen,
                domainBuffer,
                domainBufNeededLen,
                ignored);
        if (!success) {
            return NativeProcess.UNKNOWN_OWNER;
        }

        return new String(nameBuffer);
    }

    private void closeHandleIfNotNull(int handle) {
        if (handle != WinApiConstants.NULLPTR) {
            kernel.closeHandle(handle);
        }
    }
}
