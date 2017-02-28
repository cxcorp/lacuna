package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.MemoryWriter;
import cx.corp.lacuna.ui.model.BookmarkModel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BookmarkComponent {
    private final BookmarkModel model;
    private final MemoryReader reader;
    private final MemoryWriter writer;

    private JPanel panel;
    private JTable table;

    public BookmarkComponent(BookmarkModel model, MemoryReader reader, MemoryWriter writer) {
        Objects.requireNonNull(model, "model cannot be null!");
        Objects.requireNonNull(reader, "reader cannot be null!");
        Objects.requireNonNull(writer, "writer cannot be null!");
        this.model = model;
        this.reader = reader;
        this.writer = writer;
        createPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createPanel() {
        panel = new JPanel(new BorderLayout());
        createTable();
    }

    private void createTable() {
        table = new JTable();
        createAndSetTableModel();
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void createAndSetTableModel() {
        BookmarkTableModel model = new BookmarkTableModel();
        table.setModel(model);
    }
}
