package cx.corp.lacuna.ui.model;

import cx.corp.lacuna.ui.CoreDataType;

import java.util.Objects;

public final class Bookmark {
    private String name;
    private int offset;
    private CoreDataType type;
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public CoreDataType getType() {
        return type;
    }

    public void setType(CoreDataType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bookmark bookmark = (Bookmark) o;
        return offset == bookmark.offset
            && Objects.equals(name, bookmark.name)
            && type == bookmark.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offset, type);
    }
}