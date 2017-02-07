package cx.corp.lacuna.core.windows;

@FunctionalInterface
public interface ProcessOpener {
    ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException;
}
