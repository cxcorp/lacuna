package cx.corp.lacuna.core;

public class NativeProcess {

    public static final String UNKNOWN_OWNER = "";
    public static final String UNKNOWN_DESCRIPTION = "";

    private int pid;
    private String description;
    private String owner;

    public NativeProcess() { }

    public NativeProcess(int pid, String description, String owner) {
        this.pid = pid;
        this.description = description;
        this.owner = owner;
    }

    /** Gets the process identifier.
     *
     * @return The PID.
     */
    public int getPid() {
        return pid;
    }

    /** Gets the description of the process. The description may be an
     * image name or the command line used to start the process.
     *
     * @return Description of the process.
     */
    public String getDescription() {
        return description;
    }

    /** Gets the name of the owner of the process.
     *
     * @return Name of the owner of the process
     */
    public String getOwner() {
        return owner;
    }

    /** Sets the process identifier.
     *
     * @param pid The process identifier.
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /** Sets the description of the process. The description may be an
     * image name or the command line used to start the process.
     *
     * @param description The process description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Sets the name of the owner of the process.
     *
     * @param owner The owner of the process.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
