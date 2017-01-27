package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;
import java.util.List;

public interface Advapi32 extends StdCallLibrary {
    boolean openProcessToken(int processHandle, int desiredAccess, IntByReference tokenHandle);

    boolean getTokenInformation(int token,
                                int tokenInfoClass,
                                Pointer user, //TokenOwner[] data,
                                int tokenInfoBufSize,
                                IntByReference returnLength);

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
            return new ArrayList<String>() { {
                    add("user");
                    add("attributes");
                }
            };
        }
    }
}
