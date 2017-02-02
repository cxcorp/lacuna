package cx.corp.lacuna.core.windows;

public class MockProcessOpener implements ProcessOpener {

    private ProcessHandle openReturnValue;
    private boolean throwException;

    public void setOpenReturnValue(ProcessHandle openReturnValue) {
        this.openReturnValue = openReturnValue;
    }

    public void makeOpenThrowException() {
        this.throwException = true;
    }

    @Override
    public ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException {
        if (throwException) {
            throw new ProcessOpenException("");
        }
        return openReturnValue;
    }
}
