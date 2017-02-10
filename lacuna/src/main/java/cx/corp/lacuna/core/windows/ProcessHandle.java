package cx.corp.lacuna.core.windows;

/**
 * Represents an open handle to a native process.
 *
 * <p>This interface extends the {@link AutoCloseable} interface, signifying
 * that the
 * <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resources</a>
 * statement can be used to ensure that the handle is closed after processing.
 * @see ProcessOpener
 * @see ProcessDescriptionGetter
 * @see ProcessOwnerGetter
 */
public interface ProcessHandle extends AutoCloseable {
    /**
     * Gets the raw, native handle to the process.
     * @return The raw handle to the process.
     */
    int getNativeHandle();

    /**
     * {@inheritDoc}
     * Closes the process handle.
     */
    @Override
    void close(); // no exception here
}
