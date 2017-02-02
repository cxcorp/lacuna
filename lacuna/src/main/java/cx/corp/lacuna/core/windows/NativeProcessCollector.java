package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.domain.NativeProcess;

/**
 * Provides functionality for collecting the details of a native process into
 * a {@link NativeProcess} model object using the provided process identifier.
 */
@FunctionalInterface
public interface NativeProcessCollector {
    /**
     * Collects a {@link NativeProcess} model object from the provided
     * process identifier. If a detail cannot be fetched, it will be set
     * to the corresponding UNKNOWN_* constant.
     * @param pid The process identifier.
     * @return a model with the details of the specified process.
     * @see NativeProcess#UNKNOWN_DESCRIPTION
     * @see NativeProcess#UNKNOWN_OWNER
     */
    NativeProcess collect(int pid);
}
