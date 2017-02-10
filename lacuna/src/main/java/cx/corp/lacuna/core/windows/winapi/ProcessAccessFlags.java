package cx.corp.lacuna.core.windows.winapi;

/**
 * Contains Windows API process access right flags.
 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms684880(v=vs.85).aspx">Process Security and Access Rights (Windows)</a>
 * @see cx.corp.lacuna.core.windows.ProcessOpener
 */
public final class ProcessAccessFlags {
    public static final int ALL = 0x001F0FFF;
    public static final int TERMINATE = 0x00000001;
    public static final int CREATE_THREAD = 0x00000002;
    public static final int VIRTUAL_MEMORY_OPERATION = 0x00000008;
    public static final int VIRTUAL_MEMORY_READ = 0x00000010;
    public static final int VIRTUAL_MEMORY_WRITE = 0x00000020;
    public static final int DUPLICATE_HANDLE = 0x00000040;
    public static final int CREATE_PROCESS = 0x000000080;
    public static final int SET_QUOTA = 0x00000100;
    public static final int SET_INFORMATION = 0x00000200;
    public static final int QUERY_INFORMATION = 0x00000400;
    public static final int QUERY_LIMITED_INFORMATION = 0x00001000;
    public static final int SYNCHRONIZE = 0x00100000;
}