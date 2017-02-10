package cx.corp.lacuna.core.windows.winapi;

/**
 * Proxy interface to the Windows API {@code kernel32.dll} library.
 *
 * <p>In order to map the Java-style camelCase method names to the correct
 * PascalCase names, {@link CamelToPascalCaseFunctionMapper} can be used.
 * @see WinApiBootstrapper
 * @cx.winapiinterface
 */
public interface Kernel32 extends
    CloseHandle,
    OpenProcess,
    QueryFullProcessImageName,
    GetProcessId,
    ReadProcessMemory {

}