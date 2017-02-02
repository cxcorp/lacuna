package cx.corp.lacuna.core.windows;

public interface ProcessOpener {
    ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException;
}
