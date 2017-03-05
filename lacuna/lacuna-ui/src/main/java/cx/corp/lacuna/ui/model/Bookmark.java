package cx.corp.lacuna.ui.model;

import java.util.Objects;

public final class Bookmark {
    private String name;
    private int offset;

    public Bookmark() {
    }

    public Bookmark(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    public static Bookmark empty() {
        return new Bookmark(null, 0);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(name, offset);
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
            && Objects.equals(name, bookmark.name);
    }
}