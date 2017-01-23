package cx.corp.lacuna.core;

public class NativeProcess {

    public static final String UNKNOWN_OWNER = "";
    public static final String UNKNOWN_DESCRIPTION = "";

    private int pid;
    private String description;

    /** Gets the process identifier.
     *
     * @return The PID.
     */
    public int getPid() {
        return pid;
    }

    /** Gets the description of the process. The description may be an
     * image name or the command line used to start the process.
     * @return Description of the process.
     */
    public String getDescription() {
        return description;
    }

    /** Sets the process identifier.
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /** Sets the description of the process. The description may be an
     * image name or the command line used to start the process.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
