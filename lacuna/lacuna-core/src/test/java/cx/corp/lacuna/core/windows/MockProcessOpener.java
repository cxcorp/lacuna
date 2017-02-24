package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.ProcessOpenException;

public class MockProcessOpener implements ProcessOpener {

    private ProcessHandle openReturnValue;
    private boolean throwException;

    public void setOpenReturnValue(ProcessHandle openReturnValue) {
        this.openReturnValue = openReturnValue;
    }

    public void throwExceptionOnOpen() {
        this.throwException = true;
    }

    public void doNotThrowExceptionOnOpen() {
        this.throwException = false;
    }

    @Override
    public ProcessHandle open(int pid, int processAccessFlags) throws ProcessOpenException {
        if (throwException) {
            throw new ProcessOpenException("");
        }
        return openReturnValue;
    }
}
