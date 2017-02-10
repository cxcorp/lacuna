package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy interface to the Windows API {@code advapi32.dll} library.
 *
 * <p>In order to map the Java-style camelCase method names to the correct
 * PascalCase names, {@link CamelToPascalCaseFunctionMapper} can be used.
 * @see WinApiBootstrapper
 * @cx.winapiinterface
 */
public interface Advapi32 extends StdCallLibrary {
    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379295(v=vs.85).aspx">OpenProcessToken function</a>.
     */
    boolean openProcessToken(int processHandle, int desiredAccess, IntByReference tokenHandle);

    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa446671(v=vs.85).aspx">GetTokenInformation function</a>.
     */
    boolean getTokenInformation(int token,
                                int tokenInfoClass,
                                Pointer user, //TokenOwner[] data,
                                int tokenInfoBufSize,
                                IntByReference returnLength);


    /**
     * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/aa379166(v=vs.85).aspx">LookupAccountSid function</a>.
     */
    boolean lookupAccountSidW(int lpSystemName,
                              int sid,
                              char[] outNameBuffer,
                              IntByReference bufferLengthInChars,
                              char[] outDomainNameBuffer,
                              IntByReference domainLengthInChars,
                              IntByReference outSidNameUse);

    class TokenUser extends Structure {

        // JNA needs these to be public fields :(
        public int user;
        public int attributes;

        public TokenUser(Pointer ptr) {
            user = ptr.getInt(0);
            attributes = ptr.getInt(4);
        }

        @Override
        protected List<String> getFieldOrder() {
            return new ArrayList<String>() {
                {
                    add("user");
                    add("attributes");
                }
            };
        }
    }
}
