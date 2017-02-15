package cx.corp.lacuna.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IntegrationTestConstants {
    public static Path getTestTargetUrlForWindows() {
        try {
            URL url = IntegrationTestConstants.class.getClassLoader().getResource("testtarget.exe");
            URI uri = url.toURI();
            return Paths.get(uri);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
