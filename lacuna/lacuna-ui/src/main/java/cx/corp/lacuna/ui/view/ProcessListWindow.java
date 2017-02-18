package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.presenter.ProcessListCallbacks;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ProcessListWindow implements ProcessListView {

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private static final int COMPONENT_PADDING = 16;
    private static final int TABLE_PADDING_X = 16;
    private static final int TABLE_PADDING_Y = 0; // doesn't resize rows
    private static final int COLUMN_PID_INDEX = 0;

    private ProcessListCallbacks callbacks;
    private JFrame frame;
    private JPanel frameContainer;
    private NativeProcessNonEditTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JButton searchButton;
    private JButton searchClearButton;
    private JButton chooseButton;

    private Integer chosenProcessId;
    private String searchFilter;

    public ProcessListWindow() {
        chosenProcessId = null;
        searchFilter = "";
        createWindow();
    }

    public void show() {
        frame.setVisible(true);
    }

    @Override
    public Optional<Integer> getChosenProcessId() {
        return Optional.ofNullable(chosenProcessId);
    }

    @Override
    public void setProcessList(Collection<NativeProcess> processes) {
        clearTable();
        addRows(processes);
    }

    @Override
    public void attach(ProcessListCallbacks processListCallbacks) {
        callbacks = processListCallbacks;
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
        createOtherControls();
        createBindingsAndListeners();
    }

    private void createAndSetContentPane() {
        frameContainer = new JPanel(new GridBagLayout());
        frame.setContentPane(frameContainer);
    }

    private void createTable() {
        createTableModel();
        table = new JTable(tableModel);
        table.setIntercellSpacing(new Dimension(TABLE_PADDING_X, TABLE_PADDING_Y));
        sortPidColumnDescending();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.9;
        c.insets = new Insets(COMPONENT_PADDING, COMPONENT_PADDING, 0, COMPONENT_PADDING);
        JScrollPane scrollPane = new JScrollPane(table);
        frameContainer.add(scrollPane, c);
    }

    private void createTableModel() {
        NativeProcessNonEditTableModel model = new NativeProcessNonEditTableModel();
        model.addColumn("PID");
        model.addColumn("Owner");
        model.addColumn("Description");
        tableModel = model;
    }

    private void sortPidColumnDescending() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(COLUMN_PID_INDEX, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);
    }

    private void createOtherControls() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        searchField = new JTextField();
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 100;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        panel.add(searchField, c);

        searchButton = new JButton("Search");
        c.fill = GridBagConstraints.NONE;
        c.weightx = 10;
        c.gridx = 1;
        panel.add(searchButton, c);

        searchClearButton = new JButton("Clear");
        c.weightx = 10;
        c.gridx = 2;
        panel.add(searchClearButton, c);

        chooseButton = new JButton("Choose process");
        chooseButton.setEnabled(false); // disabled until a process is selected
        c.gridx = 3;
        c.anchor = GridBagConstraints.EAST;
        panel.add(chooseButton);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.weighty = 0.10;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(COMPONENT_PADDING, COMPONENT_PADDING, COMPONENT_PADDING, COMPONENT_PADDING);
        frameContainer.add(panel, c);
    }

    private void createBindingsAndListeners() {
        chooseButtonGetsEnabledWhenRowIsSelected();
        rowSelectionUpdatesChosenProcessId();
        searchFieldUpdatesSearchFilter();
        chooseButtonSendsMessageAndClosesWindow();
        searchButtonUpdatesFilter();
        searchClearButtonClearsSearchField();
    }

    private void chooseButtonGetsEnabledWhenRowIsSelected() {
        table.getSelectionModel().addListSelectionListener(
            e -> chooseButton.setEnabled(true));
    }

    private void rowSelectionUpdatesChosenProcessId() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                chosenProcessId = null;
                return;
            }
            chosenProcessId = (Integer)table.getModel().getValueAt(row, COLUMN_PID_INDEX);
        });
    }

    private void searchFieldUpdatesSearchFilter() {
        UpdateDocumentListener.addTo(searchField, text -> searchFilter = text);
    }

    private void chooseButtonSendsMessageAndClosesWindow() {
        chooseButton.addActionListener(e -> {
            if (callbacks != null) {
                callbacks.processChosen();
            }
            frame.setVisible(false);
        });
    }

    private void searchButtonUpdatesFilter() {
        searchButton.addActionListener(e -> updateTableFilter());
    }

    @SuppressWarnings("unchecked")
    private void updateTableFilter() {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)table.getRowSorter();
        sorter.setRowFilter(searchFilter.isEmpty() ? null : RowFilter.regexFilter(searchFilter));
    }

    private void searchClearButtonClearsSearchField() {
        searchClearButton.addActionListener(e -> {
            searchField.setText("");
            searchFilter = "";
            updateTableFilter();
        });
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void addRows(Collection<NativeProcess> processes) {
        processes.forEach(this.tableModel::addRow);
        new TableColumnResizer(table).resize();
    }
}
