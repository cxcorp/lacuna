package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.ui.model.Bookmark;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookmarkTableModel extends DefaultTableModel {
    private static final String[] BOOKMARK_FIELD_NAMES = {
        "Description",
        "Offset",
    };
    private static final Class<?>[] BOOKMARK_FIELD_TYPES = {
        String.class,  // description
        Integer.class, // offset
    };

    private final List<Bookmark> bookmarks = new ArrayList<>();

    public BookmarkTableModel() {
        super.setColumnIdentifiers(BOOKMARK_FIELD_NAMES);
    }

    public void addRow(Bookmark bookmark) {
        bookmarks.add(bookmark);
        super.addRow(bookmarkToCells(bookmark));
    }

    public void clearRows() {
        super.setRowCount(0);
    }

    public Bookmark getBookmark(int modelIndex) {
        return bookmarks.get(modelIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return
            columnIndex >= BOOKMARK_FIELD_TYPES.length
                ? null
                : BOOKMARK_FIELD_TYPES[columnIndex];
    }

    @Override
    public void removeRow(int modelIndex) {
        bookmarks.remove(modelIndex);
        super.removeRow(modelIndex);
    }

    private Object[] bookmarkToCells(Bookmark bookmark) {
        return new Object[]{
            bookmark.getName(),
            bookmark.getOffset()
        };
    }
}
