package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;

import java.lang.reflect.Method;

/**
 * Provides mapping from camelCase Java method names to PascalCase WinAPI names.
 */
public class CamelToPascalCaseFunctionMapper implements FunctionMapper {
    @Override
    public String getFunctionName(NativeLibrary nativeLibrary, Method method) {
        return capitalizeFirstLetter(method.getName());
    }

    private static String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
