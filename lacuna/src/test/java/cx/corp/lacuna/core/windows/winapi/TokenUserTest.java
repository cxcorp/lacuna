package cx.corp.lacuna.core.windows.winapi;

import com.sun.jna.Memory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenUserTest {
    @Test
    public void constructorCorrectlyReadsUserFieldFromPointer() {
        int userValue = 0xDEADBEEF;
        Memory memory = new Memory(16);
        memory.setInt(0, userValue);

        Advapi32.TokenUser user = new Advapi32.TokenUser(memory);
        assertEquals(userValue, user.user);
    }

    @Test
    public void constructorCorrectlyReadsAttributesFieldFromPointer() {
        int attributesValue = 0xF1F1FAFA;
        Memory memory = new Memory(16);
        memory.setInt(4, attributesValue);

        Advapi32.TokenUser user = new Advapi32.TokenUser(memory);
        assertEquals(attributesValue, user.attributes);
    }

    @Test
    public void getFieldOrderReturnsCorrectOrder() {
        Memory memory = new Memory(16);
        memory.setLong(0, 0);

        Advapi32.TokenUser user = new Advapi32.TokenUser(memory);
        List<String> fieldOrder = user.getFieldOrder();
        assertEquals("user", fieldOrder.get(0));
        assertEquals("attributes", fieldOrder.get(1));
    }
}
