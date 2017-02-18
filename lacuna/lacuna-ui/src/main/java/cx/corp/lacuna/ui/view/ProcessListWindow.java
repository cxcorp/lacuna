package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class ProcessListWindow implements ProcessListView {

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;
    private static final int COMPONENT_PADDING = 16;
    private static final int TABLE_PADDING_X = 10;
    private static final int TABLE_PADDING_Y = 5;

    private JFrame frame;
    private JPanel frameContainer;
    private DefaultTableModel tableModel;
    private JTable table;
    private int chosenProcessId;
    //private String searchFilter;

    public ProcessListWindow() {
        chosenProcessId = -1;
        //searchFilter = "";
        createWindow();
    }

    private void createWindow() {
        createFrame();
        createComponents();
        frame.pack();
    }

    private void createFrame() {
        frame = new JFrame("Process list");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    private void createComponents() {
        createAndSetContentPane();
        createTable();
        frameContainer.add(new JLabel("Ayy"));
    }

    private void createAndSetContentPane() {
        frameContainer = new JPanel(new GridBagLayout());
        frame.setContentPane(frameContainer);
    }

    private void createTable() {
        tableModel = createTableModel();
        table = new JTable(tableModel);
        sortPidColumnDescending();
        table.setIntercellSpacing(new Dimension(TABLE_PADDING_X, TABLE_PADDING_Y));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.8;
        c.insets = new Insets(COMPONENT_PADDING, COMPONENT_PADDING, 0, COMPONENT_PADDING);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, c);
    }

    private DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                Class<?>[] columnClasses = {Integer.class, String.class, String.class};
                return columnIndex > columnClasses.length ? null : columnClasses[columnIndex];
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                // make the table read-only
                return false;
            }
        };
        model.addColumn("PID");
        model.addColumn("Owner");
        model.addColumn("Description");
        return model;
    }

    private void sortPidColumnDescending() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);

        // SORT
        //sorter.setRowFilter(RowFilter.regexFilter(".*foo.*"));
    }

    @Override
    public int getChosenProcessId() {
        return chosenProcessId;
    }

    @Override
    public void setProcessList(Collection<NativeProcess> processes) {
        clearTable();
        addRows(processes);
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void addRows(Collection<NativeProcess> processes) {
        processes.forEach(this::addRow);
        new TableColumnResizer(table).resize();
    }

    private void addRow(NativeProcess process) {
        Object[] fields = {process.getPid(), process.getOwner(), process.getDescription()};
        tableModel.addRow(fields);
    }

    public void show() {
        frame.setVisible(true);
    }
}
