package cx.corp.lacuna.core.windows;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.SystemErrorCode;
import cx.corp.lacuna.core.windows.winapi.WinApiConstants;

import java.util.Objects;
import java.util.Optional;

/**
 * Looks up the user name of a {@link Advapi32.TokenUser} via the Windows
 * API {@link Advapi32} library.
 * @cx.useswinapi
 */
public class TokenOwnerNameFinder {

    private final Advapi32 advapi;

    /**
     * Constructs a new {@code TokenOwnerNameFinder} with the specified
     * Windows API {@code Advapi32} proxy.
     * @param advapi the Windows API {@code Advapi32} proxy.
     * @cx.useswinapi
     */
    public TokenOwnerNameFinder(Advapi32 advapi) {
        Objects.requireNonNull(advapi, "advapi cannot be null!");
        this.advapi = advapi;
    }

    /**
     * Gets the user name of the specified {@code TokenUser} account.
     * <p>The method functions by calling the Windows API
     * {@link Advapi32#lookupAccountSidW} method. The {@link Advapi32.TokenUser#user}
     * field is passed as the SID.
     * @param user the token user.
     * @return the name of the user account, or {@link Optional#empty()} if the
     *         lookup fails.
     * @cx.useswinapi
     */
    public Optional<String> getUserName(Advapi32.TokenUser user) {
        Objects.requireNonNull(user, "user cannot be null!");
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

    private boolean callFailedBecauseBufferWasTooSmall() {
        return Native.getLastError() == SystemErrorCode.INSUFFICIENT_BUFFER.getSystemErrorId();
    }
}
