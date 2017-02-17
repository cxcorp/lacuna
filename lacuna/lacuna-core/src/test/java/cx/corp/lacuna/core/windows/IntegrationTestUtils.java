package cx.corp.lacuna.core.windows;

import cx.corp.lacuna.core.windows.winapi.Kernel32;

import java.lang.reflect.Field;

public class IntegrationTestUtils {
    /**
     * Uses reflection to get the private handle id if this is indeed correctValuesAreReadFromTestTargetMemory Windows platform!
     * Tested on JDK 8!
     */
    public static int getPid(Kernel32 kernel32, Process process) throws NoSuchFieldException, IllegalAccessException {
        Class<? extends Process> clazz = process.getClass();
        Field handleField = clazz.getDeclaredField("handle");
        handleField.setAccessible(true);
        long handle = (Long) handleField.get(process);
        handleField.setAccessible(false);
        return kernel32.getProcessId((int) handle);
    }
}
