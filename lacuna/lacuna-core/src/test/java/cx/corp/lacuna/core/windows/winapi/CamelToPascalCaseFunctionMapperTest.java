package cx.corp.lacuna.core.windows.winapi;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class CamelToPascalCaseFunctionMapperTest {

    private CamelToPascalCaseFunctionMapper mapper;

    @Before
    public void setUp() {
        mapper = new CamelToPascalCaseFunctionMapper();
    }

    @Test
    public void capitalizesOneCharacterName() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("a");
        String mappedName = mapper.getFunctionName(null, method);
        assertEquals("A", mappedName);
    }

    @Test
    public void capitalizesLongerName() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("longerMethodNameW");
        String mappedName = mapper.getFunctionName(null, method);
        assertEquals("LongerMethodNameW", mappedName);
    }

    private static void a() {
        // Don't delete, used in tests
    }

    private static void longerMethodNameW() {
        // Don't delete, used in tests
    }
}
