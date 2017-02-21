package cx.corp.lacuna.core.domain;

import java.util.Objects;

public class NativeProcessImpl implements NativeProcess {

    public static final String UNKNOWN_OWNER = "";
    public static final String UNKNOWN_DESCRIPTION = "";

    private int pid;
    private String description;
    private String owner;

    public NativeProcessImpl() {
    }

    public NativeProcessImpl(int pid, String description, String owner) {
        this.pid = pid;
        this.description = description;
        this.owner = owner;
    }

    /**
     * Gets the process identifier.
     *
     * @return The PID.
     */
    public int getPid() {
        return pid;
    }

    /**
     * Sets the process identifier.
     *
     * @param pid The process identifier.
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * Gets the description of the process. The description may be an
     * image name or the command line used to start the process.
     *
     * @return Description of the process.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the process. The description may be an
     * image name or the command line used to start the process.
     *
     * @param description The process description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the name of the owner of the process.
     *
     * @return Name of the owner of the process
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the name of the owner of the process.
     *
     * @param owner The owner of the process.
     */
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
