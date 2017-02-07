package cx.corp.lacuna.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestTargetLauncher {

    private final Path executablePath;
    private Integer firstArgInt = 0;
    private Integer secondArgInt = 0;
    private Boolean thirdArgBoolean = false;
    private Boolean fourthArgBoolean = false;
    private Short fifthArgShort = 0;
    private String name = "name";

    public TestTargetLauncher(Path executablePath) {
        this.executablePath = executablePath;
    }

    public String getExecutableName() {
        return executablePath.getFileName().toString();
    }

    public ProcessBuilder createBuilder() {
        List<String> args = new ArrayList<>();
        args.add(executablePath.toString());
        args.add(firstArgInt.toString());
        args.add(secondArgInt.toString());
        args.add(toIntString(thirdArgBoolean));
        args.add(toIntString(fourthArgBoolean));
        args.add(fifthArgShort.toString());
        args.add(name);
        return new ProcessBuilder(args);
    }

    public TestTargetLauncher withFirstArg(int value) {
        this.firstArgInt = value;
        return this;
    }

    public TestTargetLauncher withSecondArg(int value) {
        this.secondArgInt = value;
        return this;
    }

    public TestTargetLauncher withThirdArg(boolean value) {
        this.thirdArgBoolean = value;
        return this;
    }

    public TestTargetLauncher withFourthArg(boolean value) {
        this.fourthArgBoolean = value;
        return this;
    }

    public TestTargetLauncher withFifthArg(short value) {
        this.fifthArgShort = value;
        return this;
    }

    public TestTargetLauncher withSixthArg(String value) {
        this.name = value;
        return this;
    }

    private static String toIntString(boolean val) {
        return (val ? (Integer) 1 : (Integer) 0).toString();
    }
}
