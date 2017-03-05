package cx.corp.lacuna.ui;

public enum CoreDataType {

    BOOLEAN("boolean", false),
    BYTE("byte", 0),
    CHAR_UTF8("char (UTF-8)", '\0'),
    CHAR_UTF16("char (UTF-16LE)", '\0'),
    SHORT("short", 0),
    INT("int", 0),
    LONG("long", 0),
    FLOAT("float", 0),
    DOUBLE("double", 0),
    STRING_UTF8("string (UTF-8)", ""),
    STRING_UTF16("string (UTF-16LE)", "");

    private final String humanReadableName;
    private final Object defaultValue;

    CoreDataType(String humanReadableName, Object defaultValue) {
        this.humanReadableName = humanReadableName;
        this.defaultValue = defaultValue;
    }

    public static CoreDataType fromHumanReadableName(String name) {
        for (CoreDataType type : values()) {
            if (type.getHumanReadableName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return humanReadableName;
    }
}
