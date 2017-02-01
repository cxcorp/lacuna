package cx.corp.lacuna.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestTargetLauncher {

    private final Path executablePath;
    private Integer money = 0;
    private Integer gold = 0;
    private Boolean gentooInstalled = false;
    private Boolean privilegeChecked = false;
    private Short speedOfSeriousShit = 0;
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
        args.add(money.toString());
        args.add(gold.toString());
        args.add(toIntString(gentooInstalled));
        args.add(toIntString(privilegeChecked));
        args.add(speedOfSeriousShit.toString());
        args.add(name);
        return new ProcessBuilder(args);
    }

    public TestTargetLauncher withMoney(int money) {
        this.money = money;
        return this;
    }

    public TestTargetLauncher withGold(int gold) {
        this.gold = gold;
        return this;
    }

    public TestTargetLauncher withGentooInstalled(boolean installed) {
        this.gentooInstalled = installed;
        return this;
    }

    public TestTargetLauncher withPrivilegeChecked(boolean checked) {
        this.privilegeChecked = checked;
        return this;
    }

    public TestTargetLauncher withSpeedOfSeriousShit(short val) {
        this.speedOfSeriousShit = val;
        return this;
    }

    public TestTargetLauncher withName(String name) {
        this.name = name;
        return this;
    }

    private static String toIntString(boolean val) {
        return (val ? (Integer) 1 : (Integer) 0).toString();
    }
}
