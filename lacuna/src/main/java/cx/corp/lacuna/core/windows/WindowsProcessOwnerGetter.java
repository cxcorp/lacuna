package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;

import java.util.Optional;

public class WindowsProcessOwnerGetter implements ProcessOwnerGetter {

    private final ProcessTokenOpener tokenOpener;
    private final TokenUserFinder tokenUserFinder;
    private final TokenOwnerNameFinder tokenOwnerFinder;

    public WindowsProcessOwnerGetter(Advapi32 advapi, CloseHandle handleCloser) {
        if (advapi == null || handleCloser == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        tokenOpener = new ProcessTokenOpener(advapi, handleCloser);
        tokenUserFinder = new TokenUserFinder(advapi);
        tokenOwnerFinder = new TokenOwnerNameFinder(advapi);
    }

    @Override
    public Optional<String> get(ProcessHandle processHandle) {
        if (processHandle == null) {
            throw new IllegalArgumentException("processHandle cannot be null!");
        }

        try (ProcessToken token = tokenOpener.openToken(processHandle)) {
            return tokenUserFinder.findTokenUser(token).flatMap(tokenOwnerFinder::getUserName);
        } catch (TokenOpenException ex) {
            // loggerino
            return Optional.empty();
        }
    }
}
