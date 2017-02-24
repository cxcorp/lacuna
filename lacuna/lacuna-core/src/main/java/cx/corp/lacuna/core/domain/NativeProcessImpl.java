package cx.corp.lacuna.core.domain;

import java.util.Objects;

public class NativeProcessImpl implements NativeProcess {

    /**
     * Represents an unknown owner.
     */
    public static final String UNKNOWN_OWNER = "";
    /**
     * Represents an unknown description.
     */
    public static final String UNKNOWN_DESCRIPTION = "";

    private int pid;
    private String description;
    private String owner;

    /**
     * Constructs a new {@code NativeProcessImpl}.
     */
    public NativeProcessImpl() {
    }

    /**
     * Constructs a new {@code NativeProcessImpl} with the specified process
     * identifier, description, and owner.
     * @param pid the process identifier.
     * @param description the process description.
     * @param owner the process owner.
     */
    public NativeProcessImpl(int pid, String description, String owner) {
        this.pid = pid;
        this.description = description;
        this.owner = owner;
    }

    @Override
    public int getPid() {
        return pid;
    }

    @Override
    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NativeProcessImpl process = (NativeProcessImpl) o;
        return pid == process.pid &&
            Objects.equals(description, process.description) &&
            Objects.equals(owner, process.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, description, owner);
    }

    @Override
    public String toString() {
        return String.format("{ PID: %d, owner: %s, description: %s }",
            pid,
            owner,
            description
        );
    }
}
