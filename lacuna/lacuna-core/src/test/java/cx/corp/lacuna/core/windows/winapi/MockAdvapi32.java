package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockAdvapi32 implements Advapi32 {

    private List<Integer> openProcessTokenPassedHandles = new ArrayList<Integer>();
    private boolean openProcessTokenReturnValue = false;
    private int openProcessTokenTokenHandle = 0;
    private boolean getTokenInformationReturnValue = false;
    private Memory getTokenInformationBufferData = null;
    private int getTokenInformationBufferRequiredLength = -1;

    public void setOpenProcessTokenReturnValue(boolean openProcessTokenReturnValue) {
        this.openProcessTokenReturnValue = openProcessTokenReturnValue;
    }

    public void setOpenProcessTokenTokenHandle(int openProcessTokenTokenHandle) {
        this.openProcessTokenTokenHandle = openProcessTokenTokenHandle;
    }

    public List<Integer> getOpenProcessTokenPassedHandles() {
        return openProcessTokenPassedHandles;
    }

    public void setGetTokenInformationReturnValue(boolean getTokenInformationReturnValue) {
        this.getTokenInformationReturnValue = getTokenInformationReturnValue;
    }

    public void setGetTokenInformationBufferData(Memory getTokenInformationBufferData) {
        this.getTokenInformationBufferData = getTokenInformationBufferData;
    }

    public void setGetTokenInformationBufferRequiredLength(int getTokenInformationBufferRequiredLength) {
        this.getTokenInformationBufferRequiredLength = getTokenInformationBufferRequiredLength;
    }

    @Override
    public boolean openProcessToken(int processHandle, int desiredAccess, IntByReference tokenHandle) {
        openProcessTokenPassedHandles.add(processHandle);
        if (!openProcessTokenReturnValue) {
            return false;
        }

        tokenHandle.setValue(openProcessTokenTokenHandle);
        return true;
    }

    @Override
    public boolean getTokenInformation(int token, int tokenInfoClass, Pointer user, int tokenInfoBufSize, IntByReference returnLength) {
        if (tokenInfoClass != WinApiConstants.GETTOKENINFORMATION_TOKENUSER) {
            throw new UnsupportedOperationException("Only TOKEN_USER fetches are supported!");
        }

        if (!getTokenInformationReturnValue) {
            return false;
        }

        if (user == null || tokenInfoBufSize < getTokenInformationBufferRequiredLength) {
            // WinAPI function first expects you to call with insufficient size to
            // get the actual needed size, then it calls with the correctly sized buffer
            returnLength.setValue(getTokenInformationBufferRequiredLength);
            // a "get needed buffer size call fail" is distinguished from a "call just failed"
            // by the last error code
            Native.setLastError(SystemErrorCode.INSUFFICIENT_BUFFER.getSystemErrorId());
        }

        byte[] buf = getTokenInformationBufferData.getByteArray(0, (int) getTokenInformationBufferData.size());
        int len = Math.min(getTokenInformationBufferRequiredLength, (int) getTokenInformationBufferData.size());
        user.write(0, buf, 0, len);
        return true;
    }

    @Override
    public boolean lookupAccountSidW(int lpSystemName, int sid, char[] outNameBuffer, IntByReference bufferLengthInChars, char[] outDomainNameBuffer, IntByReference domainLengthInChars, IntByReference outSidNameUse) {
        return false;
    }
}
