package cx.corp.lacuna.core.windows;

import java.util.Optional;

public class WindowsProcessOwnerGetter implements ProcessOwnerGetter {

    private final ProcessTokenOpener tokenOpener;
    private final TokenUserFinder tokenUserFinder;
    private final TokenOwnerNameFinder tokenOwnerFinder;

    /**
     * Constructs a new {@code WindowsProcessOwnerGetter} with the specified
     * process token opener, token user finder, and user name finder.
     * @param tokenOpener the process token opener.
     * @param userFinder the token user finder.
     * @param nameFinder the user name finder.
     * @throws IllegalArgumentException if any of the arguments are null.
     */
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
