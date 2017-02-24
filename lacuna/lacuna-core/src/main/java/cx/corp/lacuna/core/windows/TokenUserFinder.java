package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Optional;

/**
 * {@inheritDoc}
 * @cx.useswinapi
 */
public class TokenUserFinder {

    private final Advapi32 advapi;

    public TokenUserFinder(Advapi32 advapi) {
        if (advapi == null) {
            throw new IllegalArgumentException("Advapi can't be null");
        }
        this.advapi = advapi;
    }

    public Optional<Advapi32.TokenUser> findTokenUser(ProcessToken token) {
        return getNeededTokenUserInfoBufferLength(token)
            .flatMap(bufLen -> getTokenUserInfo(token, bufLen));
    }

    private Optional<Integer> getNeededTokenUserInfoBufferLength(ProcessToken processToken) {
        IntByReference bytesNeeded = new IntByReference(0);
        boolean success =
            advapi.getTokenInformation(
                processToken.getToken(),
                WinApiConstants.GETTOKENINFORMATION_TOKENUSER,
                null,
                0,
                bytesNeeded);

        return !success && callFailedBecauseBufferWasTooSmall()
            ? Optional.of(bytesNeeded.getValue())
            : Optional.empty();
    }

    private Optional<Advapi32.TokenUser> getTokenUserInfo(ProcessToken token, int infoBufferLength) {
        Memory memory = new Memory(infoBufferLength);
        IntByReference bufferLen = new IntByReference(infoBufferLength);
        boolean success =
            advapi.getTokenInformation(
                token.getToken(),
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

    private boolean callFailedBecauseBufferWasTooSmall() {
        return Native.getLastError() == SystemErrorCode.INSUFFICIENT_BUFFER.getSystemErrorId();
    }
}
