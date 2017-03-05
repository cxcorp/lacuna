package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.MemoryWriter;
import cx.corp.lacuna.ui.model.Bookmark;
import cx.corp.lacuna.ui.model.BookmarkModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class BookmarkComponent {
    private final BookmarkModel model;
    private final MemoryReader reader;
    private final MemoryWriter writer;

    private JPanel panel;
    private JTable table;
    private BookmarkTableModel tableModel;

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

    //<editor-fold desc="components">
    private void createPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        createTable();
        createButtons();
    }

    private void createTable() {
        table = new JTable();
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            "delete"
        );
        table.getActionMap().put(
            "delete",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeSelectedTableRow();
                }
            }
        );
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        createAndSetTableModel();
        createTablePopupMenu();
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
    }

    private void createTablePopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteItem.addActionListener(l -> removeSelectedTableRow());
        menu.add(deleteItem);
        table.setComponentPopupMenu(menu);

    }

    private void createAndSetTableModel() {
        tableModel = new BookmarkTableModel();
        table.setModel(tableModel);
    }

    private void createButtons() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton addRowButton = new JButton("New Bookmark");
        addRowButton.addActionListener(e -> tableModel.addRow(Bookmark.empty()));
        buttonsPanel.add(addRowButton);
        buttonsPanel.setMinimumSize(new Dimension(0, 40)); // RIP different DPIs
        panel.add(buttonsPanel);
    }
    //</editor-fold>

    private void removeSelectedTableRow() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        // view index is not necessarily the same as the model's index,
        // for example if the table is sorted or filtered, the index will differ
        int modelIndex = table.convertRowIndexToModel(row);
        tableModel.removeRow(modelIndex);
    }

    private void refreshValues() {

    }
}
