package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.Advapi32;
import cx.corp.lacuna.core.windows.winapi.CloseHandle;

import java.util.Optional;

public class WindowsProcessOwnerGetter implements ProcessOwnerGetter {

    private final ProcessTokenOpener tokenOpener;
    private final TokenUserFinder tokenUserFinder;
    private final TokenOwnerNameFinder tokenOwnerFinder;

    public WindowsProcessOwnerGetter(ProcessTokenOpener tokenOpener,
                                     TokenUserFinder userFinder,
                                     TokenOwnerNameFinder nameFinder) {
        if (tokenOpener == null || userFinder == null || nameFinder == null) {
            throw new IllegalArgumentException("Args can't be null");
        }
        this.tokenOpener = tokenOpener;
        this.tokenUserFinder = userFinder;
        this.tokenOwnerFinder = nameFinder;
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
