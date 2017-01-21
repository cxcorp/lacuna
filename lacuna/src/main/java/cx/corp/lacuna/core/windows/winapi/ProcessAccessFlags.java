package cx.corp.lacuna.core.windows.winapi;

public final class ProcessAccessFlags {
    public static final int All = 0x001F0FFF;
    public static final int Terminate = 0x00000001;
    public static final int CreateThread = 0x00000002;
    public static final int VirtualMemoryOperation = 0x00000008;
    public static final int VirtualMemoryRead = 0x00000010;
    public static final int VirtualMemoryWrite = 0x00000020;
    public static final int DuplicateHandle = 0x00000040;
    public static final int CreateProcess = 0x000000080;
    public static final int SetQuota = 0x00000100;
    public static final int SetInformation = 0x00000200;
    public static final int QueryInformation = 0x00000400;
    public static final int QueryLimitedInformation = 0x00001000;
    public static final int Synchronize = 0x00100000;
}