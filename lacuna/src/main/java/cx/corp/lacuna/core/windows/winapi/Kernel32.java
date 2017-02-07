package cx.corp.lacuna.core.windows.winapi;

public interface Kernel32 extends
    CloseHandle,
    OpenProcess,
    QueryFullProcessImageName,
    GetProcessId,
    ReadProcessMemory {

}