package cx.corp.lacuna.core.windows.winapi;

public final class WinApiConstants {

    /** The maximum amount of processes to support.
     * The Windows API makes no guarantees about this, but faux-empirical testing shows that
     * if you have 65536 processes running simultaneously, you've never even heard of Lacuna.
     */
    public static final int MAX_PROCESSES_SUPPORTED = 65536;

    /** Size of an int on the supported platform.
     */
    public static final int SIZEOF_INT = 4;

    /** Depicts a NULL pointer as returned by certain API functions.
     */
    public static final int NULLPTR = 0;
}
