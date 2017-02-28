package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.ui.model.Bookmark;

import javax.swing.table.DefaultTableModel;

public class BookmarkTableModel extends DefaultTableModel {
    private static final String[] BOOKMARK_FIELD_NAMES = {
        "Description",
        "Offset",
        "Type",
        "Value"
    };
    private static final Class<?>[] BOOKMARK_FIELD_TYPES = {
        String.class,  // description
        Integer.class, // offset
        Object.class,  // drop-down, CoreDataType
        Object.class // value
    };

    public BookmarkTableModel() {
        super.setColumnIdentifiers(BOOKMARK_FIELD_NAMES);
    }

    public void addRow(Bookmark bookmark) {
        super.addRow(bookmarkToCells(bookmark));
    }

    private Object[] bookmarkToCells(Bookmark bookmark) {
        return new Object[]{
            bookmark.getName(),
            bookmark.getOffset(),
            bookmark.getType(),
            bookmark.getValue()
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return
            columnIndex >= BOOKMARK_FIELD_TYPES.length
                ? null
                : BOOKMARK_FIELD_TYPES[columnIndex];
    }
}
