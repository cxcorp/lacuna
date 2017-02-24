package cx.corp.lacuna.core.windows;

import com.sun.jna.Memory;
import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.MockAdvapi32;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WindowsProcessOwnerGetterTest {

    private MockAdvapi32 advapi;
    private CloseHandle closeHandle;
    private MockProcessHandle procHandle;

    private ProcessTokenOpener opener;
    private TokenUserFinder userFinder;
    private TokenOwnerNameFinder nameFinder;
    private WindowsProcessOwnerGetter getter;

    @Before
    public void setUp() {
        advapi = new MockAdvapi32();
        closeHandle = handle -> true;
        procHandle = new MockProcessHandle(1);

        opener = new ProcessTokenOpener(advapi, closeHandle);
        userFinder = new TokenUserFinder(advapi);
        nameFinder = new TokenOwnerNameFinder(advapi);
        getter = new WindowsProcessOwnerGetter(opener, userFinder, nameFinder);
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfTokenOpenerIsNull() {
        new WindowsProcessOwnerGetter(
            null,
            userFinder,
            nameFinder
        );
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfUserFinderIsNull() {
        new WindowsProcessOwnerGetter(
            opener,
            null,
            nameFinder
        );
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNameFinderIsNull() {
        new WindowsProcessOwnerGetter(
            opener,
            userFinder,
            null
        );
    }

    @Test(expected = NullPointerException.class)
    public void getThrowsIfHandleIsNull() {
        getter.get(null);
    }

    @Test
    public void getReturnsEmptyIfTokenOpenFails() {
        advapi.setOpenProcessTokenReturnValue(false);
        Optional<String> ret = getter.get(procHandle);
        assertFalse(ret.isPresent());
    }

    @Test
    public void getReturnsEmptyIfTokenUserCannotBeFound() {
        advapi.setOpenProcessTokenReturnValue(true); // openToken succeeds
        advapi.setOpenProcessTokenTokenHandle(123);
        TokenUserFinder nonFinder = new TokenUserFinder(advapi) {
            @Override
            public Optional<Advapi32.TokenUser> findTokenUser(ProcessToken token) {
                return Optional.empty();
            }
        };
        getter = new WindowsProcessOwnerGetter(opener, nonFinder, nameFinder);
        Optional<String> ret = getter.get(procHandle);
        assertFalse(ret.isPresent());
    }

    @Test
    public void getReturnsEmptyIfUserNameCannotBeFound() {
        advapi.setOpenProcessTokenReturnValue(true); // openToken succeeds
        advapi.setOpenProcessTokenTokenHandle(123);
        TokenUserFinder userFinder = new TokenUserFinder(advapi) {
            @Override
            public Optional<Advapi32.TokenUser> findTokenUser(ProcessToken token) {
                Memory memory = new Memory(8);
                memory.write(0, new int[] {123, 321}, 0, 2);
                return Optional.of(new Advapi32.TokenUser(memory));
            }
        };
        TokenOwnerNameFinder nameNonFinder = new TokenOwnerNameFinder(advapi) {
            @Override
            public Optional<String> getUserName(Advapi32.TokenUser user) {
                return Optional.empty();
            }
        };
        getter = new WindowsProcessOwnerGetter(opener, userFinder, nameNonFinder);
        Optional<String> ret = getter.get(procHandle);
        assertFalse(ret.isPresent());
    }

    @Test
    public void getReturnsCorrectOwnerName() {
        advapi.setOpenProcessTokenReturnValue(true); // openToken succeeds
        advapi.setOpenProcessTokenTokenHandle(123);
        final String ownerName = "toaster";
        TokenUserFinder userFinder = new TokenUserFinder(advapi) {
            @Override
            public Optional<Advapi32.TokenUser> findTokenUser(ProcessToken token) {
                Memory memory = new Memory(8);
                memory.write(0, new int[] {123, 321}, 0, 2);
                return Optional.of(new Advapi32.TokenUser(memory));
            }
        };
        TokenOwnerNameFinder nameFinder = new TokenOwnerNameFinder(advapi) {
            @Override
            public Optional<String> getUserName(Advapi32.TokenUser user) {
                return Optional.of(ownerName);
            }
        };
        getter = new WindowsProcessOwnerGetter(opener, userFinder, nameFinder);
        Optional<String> ret = getter.get(procHandle);
        assertEquals(ownerName, ret.get());
    }
}
