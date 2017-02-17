package cx.corp.lacuna.core.linux;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CmdlineFileParserTest {

    private CmdlineFileParser parser;

    @Before
    public void setUp() {
        parser = new CmdlineFileParser();
    }

    @Test
    public void parseReturnsEmptyIfContentsAreNull() {
        Optional<String> result = parser.parse(null);
        assertFalse(result.isPresent());
    }

    @Test
    public void parseReturnsPresentButEmptyStringIfContentsAreEmpty() {
        Optional<String> result = parser.parse("");
        assertEquals("", result.get());
    }

    @Test
    public void parseReturnsEmptyStringIfSourceStringIsJustNulls() {
        String args = "\0\0\0\0\0\0\0";
        String result = parser.parse(args).get();
        assertEquals("", result);
    }

    @Test
    public void parseReturnsCorrectOneArgValue() {
        String args = "toasters.sh";
        String cmdLine = args + '\0';
        String result = parser.parse(cmdLine).get();
        assertEquals(args, result);
    }

    @Test
    public void parseReturnsCorrectThreeArgValue() {
        String args = "toasters.sh --run-as=bloat --force";
        String cmdLine = "toasters.sh\0--run-as=bloat\0--force\0";
        String result = parser.parse(cmdLine).get();
        assertEquals(args, result);
    }

    @Test
    public void parseReturnsCorrectStringIfArgsContainSpaces() {
        String expected = "woot woot -force";
        String cmdLine = "woot woot\0-force\0";
        String result = parser.parse(cmdLine).get();
        assertEquals(expected, result);
    }

    @Test
    public void parseReturnsCorrectStringIfArgEndsInSpace() {
        String expected = "ayylmaoo yippsss ";
        String cmdLine = "ayylmaoo\0yippsss \0";
        String result = parser.parse(cmdLine).get();
        assertEquals(expected, result);
    }

    @Test
    public void parseReturnsCorrectArgWithMoreNullsAtEnd() {
        String cmdLine = "toasters.sh\0ayy lmaoo\0\0\0\0\0\0\0\0";
        String result = parser.parse(cmdLine).get();
        assertEquals("toasters.sh ayy lmaoo", result);
    }
}
