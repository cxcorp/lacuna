package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.CloseHandle;
import cx.corp.lacuna.core.windows.winapi.MockAdvapi32;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProcessTokenTest {

    private MockProcessHandle procHandle;
    private MockAdvapi32 advapi;
    private CloseHandle closeHandle;
    private ProcessTokenOpener opener;

    @Before
    public void setUp() {
        procHandle = new MockProcessHandle(1234);
        advapi = new MockAdvapi32();
        closeHandle = null;
        CloseHandle proxyCloser = handle -> closeHandle.closeHandle(handle);
        opener = new ProcessTokenOpener(advapi, proxyCloser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfAdvapiIsNull() {
        CloseHandle nonNull = handle -> true;
        new ProcessTokenOpener(null, nonNull);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIfCloseHandleIsNull() {
        new ProcessTokenOpener(advapi, null);
    }

    @Test(expected = TokenOpenException.class)
    public void openThrowsIfTokenCannotBeOpened() {
        advapi.setOpenProcessTokenReturnValue(false);
        opener.openToken(procHandle);
    }

    @Test
    public void openOpensTokenCorrectly() {
        int expectedToken = 5562;
        advapi.setOpenProcessTokenReturnValue(true);
        advapi.setOpenProcessTokenTokenHandle(expectedToken);

        ProcessToken token = opener.openToken(procHandle);
        assertEquals(expectedToken, token.getToken());
    }

    @Test
    public void openedTokenCloseClosesToken() {
        int token = 9488;
        advapi.setOpenProcessTokenReturnValue(true);
        advapi.setOpenProcessTokenTokenHandle(token);
        List<Integer> closedTokens = new ArrayList<>();
        closeHandle = closedTokens::add;

        ProcessToken openToken = opener.openToken(procHandle);
        openToken.close();

        assertEquals(1, closedTokens.size());
        assertTrue(closedTokens.contains(token));
    }

    @Test
    public void openedTokenClosesItselfAfterTryWithResourcesBlock() {
        int token = 9488;
        advapi.setOpenProcessTokenReturnValue(true);
        advapi.setOpenProcessTokenTokenHandle(token);
        List<Integer> closedTokens = new ArrayList<>();
        closeHandle = closedTokens::add;

        try (ProcessToken openToken = opener.openToken(procHandle)) {
            assertEquals(token, openToken.getToken());
        }

        assertEquals(1, closedTokens.size());
        assertTrue(closedTokens.contains(token));
    }

    @Test
    public void openerOpensTokenForCorrectHandle() {
        int handle = 41243;
        procHandle.setNativeHandle(handle);
        advapi.setOpenProcessTokenReturnValue(true);
        advapi.setOpenProcessTokenTokenHandle(1234);

        opener.openToken(procHandle);

        List<Integer> passedHandles = advapi.getOpenProcessTokenPassedHandles();
        assertEquals(1, passedHandles.size());
        assertTrue(passedHandles.contains(handle));
    }
}
