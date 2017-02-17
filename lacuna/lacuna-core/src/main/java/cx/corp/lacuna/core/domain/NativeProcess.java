package cx.corp.lacuna.core.domain;

/**
 * Represents a native process running on the host computer.
 *
 * <p>This model object contains the process id, owner name and description
 * of the represented process.
 * @see cx.corp.lacuna.core.NativeProcessEnumerator
 * @see cx.corp.lacuna.core.NativeProcessCollector
 */
public interface NativeProcess {
    /**
     * Value used to indicate that the owner of this process is unknown.
     */
    String UNKNOWN_OWNER = "";

    /**
     * Value used to indicate that the description of this process is unknown.
     */
    String UNKNOWN_DESCRIPTION = "";

    /**
     * Gets the process identifier of the process.
     */
    int getPid();

    /**
     * Sets the process identifier of the process.
     */
    void setPid(int pid);

    /**
     * Gets the description of the process.
     *
     * <p>On Linux platforms, this value
     * is the <i>command line</i> of the running process, whereas on Windows
     * platforms this value is the image name of the process's executable.
     *
     * <p>If the process cannot be accessed, this value may be null or empty.
     * @return The description of the process, or {@link #UNKNOWN_DESCRIPTION} if
     *         the description is unknown.
     */
    String getDescription();

    /**
     * Sets the description of the process.
     */
    void setDescription(String desc);

    /**
     * Gets the owner of the process.
     *
     * <p>If the process cannot be accessed, this value may be null or empty.
     * @return The owner of the process, or {@link #UNKNOWN_OWNER} if
     *         the owner is unknown.
     */
    String getOwner();

    /**
     * Sets the name of the process's owner.
     */
    void setOwner(String owner);
}
