package cx.corp.lacuna.ui;

public enum CoreDataType {

    BOOLEAN("boolean"),
    BYTE("byte"),
    CHAR_UTF8("char (UTF-8)"),
    CHAR_UTF16("char (UTF-16LE)"),
    SHORT("short"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING_UTF8("string (UTF-8)"),
    STRING_UTF16("string (UTF-16LE)"),
    BYTE_ARRAY("array of bytes");

    private final String humanReadableName;

    CoreDataType(String humanReadableName) {
        this.humanReadableName = humanReadableName;
    }

    @Override
    public String toString() {
        return humanReadableName;
    }
}
